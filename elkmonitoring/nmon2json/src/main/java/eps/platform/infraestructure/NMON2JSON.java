package eps.platform.infraestructure;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import eps.platform.infraestructure.cli.ApplicationCLI;
import eps.platform.infraestructure.common.Utils;
import eps.platform.infraestructure.exception.EPSMonioringException;
import eps.platform.infraestructure.exception.ErrorCode;
import eps.platform.infraestructure.nmon.NMONFile;
import eps.platform.infraestructure.nmon.csv.InMemoryDB;
import eps.platform.infraestructure.nmon.json.JSONFile;
import eps.platform.infraestructure.output.OutputManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NMON2JSON {
	public static final String APPPLICATION = "NMON2JSON";
	public static final String WHITELIST_DATE_PATTERN = "yyyy-MM-dd";	
	public static final DateTimeFormatter WHITELIST_DATE_FORMATTER = DateTimeFormatter.ofPattern(WHITELIST_DATE_PATTERN);

	private static final Logger consolelogger = LoggerFactory.getLogger("Console");
	
	@Getter private int filesProcessed = 0;
	
	final public static synchronized String getVersion() {		
	    // Try to get version number from pom.xml (available in Eclipse)
	    try {
	        String className = NMON2JSON.class.getName();
	        String classfileName = "/" + className.replace('.', '/') + ".class";
	        URL classfileResource = NMON2JSON.class.getResource(classfileName);
	        if (classfileResource != null) {
	            Path absolutePackagePath = Paths.get(classfileResource.toURI())
	                    .getParent();
	            int packagePathSegments = className.length()
	                    - className.replace(".", "").length();
	            
	            // Remove package segments from path, plus two more levels
	            // for "target/classes", which is the standard location for
	            // classes in Eclipse.
	            Path path = absolutePackagePath;
	            for (int i = 0, segmentsToRemove = packagePathSegments + 2;
	                    i < segmentsToRemove; i++) {
	                path = path.getParent();
	            }
	            Path pom = path.resolve("pom.xml");
	            try (InputStream is = Files.newInputStream(pom)) {
	                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	                doc.getDocumentElement().normalize();
	                String version = (String) XPathFactory.newInstance()
	                        .newXPath().compile("/project/version")
	                        .evaluate(doc, XPathConstants.STRING);
	                if (version != null) {
	                    version = version.trim();
	                    if (!version.isEmpty()) {
	                        return version;
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        // Ignore
	    }
		
		// Try to get version number from maven properties in jar's META-INF
	    try {
	    	Class<?> cls = Class.forName("eps.platform.infraestructure.NMON2JSON");
	    	
	    	cls.getName();
	    	
	    	ClassLoader cLoader = cls.getClassLoader();	    	
	        Properties p = new Properties();
	        InputStream is = cLoader.getResourceAsStream("/META-INF/maven/" + cls.getPackage() + "/" + cls.getSimpleName() + "/pom.properties");
	        
	        if (is != null) {
	            p.load(is);
	            return p.getProperty("version", "");
	        }
	    } catch (Exception e) {
	        // ignore
	    }

	    // Fallback to using Java API to get version from MANIFEST.MF
	    try {
	    	Class<?> cls = Class.forName("eps.platform.infraestructure.NMON2JSON");
	        Package aPackage = cls.getPackage();
	        if (aPackage != null) {
	        	String version = aPackage.getImplementationVersion();
	            if (version == null) {
	                return version;
	            }
	        }
	    } catch (Exception e) {
	        // ignore
	    }		        

	    return "";
	} 	
	
	// Assumptions:
	// Folder structure: DATA_CENTRE | YYYY-MM | YYYY-MM-DD | FILE
	// FILE NAME: HOSTNAME_YYMMDD_00000.mon
	public static void main(String[] args) {
		try {
			new NMON2JSON().run(args);
			System.exit(0);
		} catch (EPSMonioringException epsMonioringException) {
			String stackTrace = Utils.stack2String(epsMonioringException);
			
			log.error("Exception {} - {}", epsMonioringException.getCode(), epsMonioringException.getMessage());
			log.error(stackTrace);
			System.exit(-1);
		} catch (Exception e) {
			String stackTrace = Utils.stack2String(e);
			
			log.error("Exception {} - {}", e.getClass().getName(), e.getMessage());
			log.error(stackTrace);
			System.exit(-1);
		}
	}

	public void run(String[] args) throws EPSMonioringException {
		Instant start = Instant.now();
		filesProcessed = 0;  // Initialize in case we use the same object to process several times - i.e. JUnit tests

		// Process arguments
		ApplicationCLI applicationCLI = new ApplicationCLI();
		boolean checkConfig = applicationCLI.checkConfig(args);
		if (!checkConfig) {
			// Want to use the help in console not in a log
			consolelogger.info(ApplicationCLI.printHelp());
			throw new EPSMonioringException(ErrorCode.INVALID_ARGUMENTS);
		}

		// Help argument
		if (applicationCLI.isSwHelp()) {
			// Want to use the help in console not in a log
			consolelogger.info(ApplicationCLI.printHelp());
			return;
		}
		
		// Version argument
		if (applicationCLI.getCommandLine().hasOption(ApplicationCLI.VERSION_OPTION_SHORT)) {
			consolelogger.info(String.format("%s version %s", NMON2JSON.APPPLICATION, getVersion()));
			return;
		}
		
		// Load Hostnames whitelist
		List<String> hostNamesWL = null;
		if (applicationCLI.getServerListFile() != null) {
			hostNamesWL = loadHostnamesList(applicationCLI.getServerListFile());
		} else {
			if (!StringUtils.isEmpty(applicationCLI.getServer())) {
				hostNamesWL = new ArrayList<>();
				hostNamesWL.add(applicationCLI.getServer());
				log.info("Processing host {}", applicationCLI.getServer());
			}
		}

		// Load Dates whitelist
		List<LocalDate> datesWL = null;
		if (applicationCLI.getDateListFile() != null) {
			datesWL = loadDatesList(applicationCLI.getDateListFile());
		} else {
			if (applicationCLI.getDate() != null) {
				datesWL = new ArrayList<>();
				datesWL.add(applicationCLI.getDate());
				log.info("Processing date {}", applicationCLI.getDate());
			}					
		}
		
		// Load Reference data
		loadReference(applicationCLI);
		
		if (!applicationCLI.getCommandLine().hasOption(ApplicationCLI.CHECK_OPTION_SHORT)) {
			// Obtain list of NMON files to process
			List <FileNameComponents> nmons = null;
			try {
				FilesToProcess ftp = FilesToProcess.builder().hostNamesWL(hostNamesWL).datesWL(datesWL).build();
				nmons = ftp.find(applicationCLI.getInputPath(), applicationCLI.getDataCentre());
			} catch (Exception e) {
				throw new EPSMonioringException("Error obtaining the list of nmon files to process", 
						e, 
						ErrorCode.INVALID_NMON_LIST);
			}
			
			// Process NMON files
			for (FileNameComponents nmon : nmons) {
				try {
					NMONFile nmonFile = new NMONFile(nmon.getInputFile(), nmon.getDataCentre());				
					nmonFile.process();
					JSONFile json = new JSONFile(nmonFile, applicationCLI.getOutputPath(), applicationCLI.isSwAppend());
					json.process();
					this.filesProcessed++;			
				} catch (Exception e) {
					log.error("Error processing nmon file {}", nmon);
					String stackTrace = Utils.stack2String(e);
					log.error(stackTrace);
				}
			}
		
			// Close all the print writers used to write the JSON files
			OutputManager.closeAll();
		}

		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		
		log.info(StringUtils.repeat('-', 80));
		log.info("End process");
		log.info(StringUtils.repeat('-', 80));
		log.info("Number of files processed: {}", this.filesProcessed, duration.getSeconds());
		log.info("Total time: {} s", this.filesProcessed, duration.getSeconds());
		log.info(StringUtils.repeat('-', 80));		
	}

	private List<String> loadHostnamesList(Path path) throws EPSMonioringException {
		List<String> hostNamesWL = null;
		
		if (path.toFile().isFile()) {
			try {
				hostNamesWL = Utils.readFiletoList(path);
				Collections.sort(hostNamesWL);
				log.info("Processing hostnames whitelist [{}]", String.join(",", hostNamesWL));
			} catch (IOException e) {
				throw new EPSMonioringException(String.format("Error processing hostnames whitelist %s", path.toString()), 
						e, 
						ErrorCode.INVALID_HOSTNAMES_WHITELIST);
			}
		}
		return hostNamesWL;
	}
	
	// applicationCLI.getDateListFile()
	public List<LocalDate> loadDatesList(Path path) throws EPSMonioringException {
		List<LocalDate> datesWL = null;
		
		if (path.toFile().isFile()) {
			try {
				datesWL = new ArrayList<>();				
				List<String> datesWLSt= Utils.readFiletoList(path);
				for (String dateSt : datesWLSt) {
					LocalDate date = parseDate(dateSt);
					if (date != null) {
						datesWL.add(date);
					}
				}				
				Collections.sort(datesWL);
			} catch (IOException e) {
				throw new EPSMonioringException(String.format("Error processing dates whitelist %s", path.toString()), 
						e, 
						ErrorCode.INVALID_DATES_WHITELIST);
			}
		}
		return datesWL;
	}

	private LocalDate parseDate(String dateSt) {
		LocalDate date = null;
		try {
			date = LocalDate.parse(dateSt, WHITELIST_DATE_FORMATTER);
		} catch (Exception e) {
			log.error("String {} in Date whitelist file is not a valid date. Expected format {}", dateSt, WHITELIST_DATE_PATTERN);
		}
		return date;
	}	
	
	public void loadReference(ApplicationCLI applicationCLI) throws EPSMonioringException {
		if (applicationCLI.getReferenceCSVPath() != null) {
			// Load reference
			try {
				InMemoryDB.loadReferenceCSV(applicationCLI.getReferenceCSVPath());
				log.info("Infrastructure reference CSV loaded [{}]", applicationCLI.getReferenceCSVPath());
			} catch (Exception e) {
				throw new EPSMonioringException(String.format("Error processing infrastructure reference CSV %s", applicationCLI.getReferenceCSVPath()), 
						e, 
						ErrorCode.INVALID_REFERECENCE_CSV);
			}
		}		
	}
	
//	public synchronized static final String getVersion() {
//		
//	    // Try to get version number from pom.xml (available in Eclipse)
//	    try {
//	        String className = NMON2JSON.class.getName();
//	        String classfileName = File.pathSeparator + className.replace(".", File.pathSeparator) + ".class";
//	        URL classfileResource = NMON2JSON.class.getResource(classfileName);
//	        if (classfileResource != null) {
//	            Path absolutePackagePath = Paths.get(classfileResource.toURI())
//	                    .getParent();
//	            int packagePathSegments = className.length()
//	                    - className.replace(".", "").length();
//	            
//	            // Remove package segments from path, plus two more levels
//	            // for "target/classes", which is the standard location for
//	            // classes in Eclipse.
//	            Path path = absolutePackagePath;
//	            for (int i = 0, segmentsToRemove = packagePathSegments + 2;
//	                    i < segmentsToRemove; i++) {
//	                path = path.getParent();
//	            }
//	            Path pom = path.resolve("pom.xml");
//	            try (InputStream is = Files.newInputStream(pom)) {
//	                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
//	                doc.getDocumentElement().normalize();
//	                String version = (String) XPathFactory.newInstance()
//	                        .newXPath().compile("/project/version")
//	                        .evaluate(doc, XPathConstants.STRING);
//	                if (version != null) {
//	                    version = version.trim();
//	                    if (!version.isEmpty()) {
//	                        return version;
//	                    }
//	                }
//	            }
//	        }
//	    } catch (Exception e) {
//	        // Ignore
//	    }
//
//	    // Try to get version number from maven properties in jar's META-INF
//	    try (InputStream is = NMON2JSON.class.getResourceAsStream("/META-INF/maven/" + MAVEN_PACKAGE + "/" + MAVEN_ARTIFACT + "/pom.properties")) {
//	        if (is != null) {
//	            Properties p = new Properties();
//	            p.load(is);
//	            String version = p.getProperty("version", "").trim();
//	            if (!version.isEmpty()) {
//	                return version;
//	            }
//	        }
//	    } catch (Exception e) {
//	        // Ignore
//	    }
//
//	    // Fallback to using Java API to get version from MANIFEST.MF
//	    String version = null;
//	    Package pkg = getClass().getPackage();
//	    if (pkg != null) {
//	        version = pkg.getImplementationVersion();
//	        if (version == null) {
//	            version = pkg.getSpecificationVersion();
//	        }
//	    }
//	    version = version == null ? "" : version.trim();
//	    return version.isEmpty() ? "unknown" : version;
//	}	
}

