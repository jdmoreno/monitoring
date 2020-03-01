package eps.platform.infraestructure.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import eps.platform.infraestructure.NMON2JSON;
import eps.platform.infraestructure.common.Utils;
import eps.platform.infraestructure.exception.ErrorCode;
import eps.platform.infraestructure.exception.EPSMonioringException;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class ApplicationCLI {
	
	protected static final String HELP_OPTION = "help";

	protected static final String SERVERLIST_OPTION_LONG = "hostnameListFile";
	public static final String SERVERLIST_OPTION_SHORT = "hl";
	
	protected static final String DATELIST_OPTION_LONG = "dateListFile";
	protected static final String DATELIST_OPTION_SHORT = "dl";

	protected static final String SERVER_OPTION_LONG = "hostname";
	protected static final String SERVER_OPTION_SHORT = "hn";
	
	// Absolute date, followed by a data
	protected static final String DATE_OPTION_LONG = "dateAbsolute";	
	protected static final String DATE_OPTION_SHORT = "da";

	// Relative date from current date, followed by {integer} to calculate current date - {integer}
	protected static final String RELATIVE_DATE_OPTION_LONG = "dateRelative";	
	protected static final String RELATIVE_DATE_OPTION_SHORT = "dr";	
	
	protected static final String INPUTPATH_OPTION_LONG = "inputPath";
	protected static final String INPUTPATH_OPTION_SHORT = "i";
	
	protected static final String OUTPUTPATH_OPTION_LONG = "outputPath";
	protected static final String OUTPUTPATH_OPTION_SHORT = "o";
	
	protected static final String DATACENTRE_OPTION_LONG = "datacentre";
	protected static final String DATACENTRE_OPTION_SHORT = "dc";
	
	protected static final String REFERENCE_OPTION_LONG = "reference";
	protected static final String REFERENCE_OPTION_SHORT = "r";
	
	protected static final String APPEND_OPTION_LONG = "append";
	protected static final String APPEND_OPTION_SHORT = "a";
	
	protected static final String VERSION_OPTION_LONG = "version";
	public static final String VERSION_OPTION_SHORT = "v";

	protected static final String CHECK_OPTION_LONG = "check";
	public static final String CHECK_OPTION_SHORT = "ch";
	
	@Getter private boolean swHelp = false;
	@Getter private boolean swLog = false;
	@Getter private boolean swAppend = false;		
	
	@Getter private Path serverListFile = null;		
	@Getter private Path inputPath = null;
	@Getter private Path outputPath = null;
	@Getter private Path dateListFile = null;
	@Getter private Path referenceCSVPath = null;

	@Getter private String dataCentre = null;	
	@Getter private String server = null;	
	@Getter private LocalDate date = null;

	@Getter private CommandLine commandLine = null;
	
	private static final String DATE_OPTIONS = String.format("Only one of %s, %s, %s can be defined as argument", DATELIST_OPTION_SHORT, DATELIST_OPTION_SHORT, RELATIVE_DATE_OPTION_SHORT);
	
	/**
	 * "Definition" stage of command-line parsing with Apache Commons CLI.
	 * @return Definition of command-line options.
	 */	
	private static Options generateOptions() {
		

	   final Option helpOption = Option.builder(HELP_OPTION)
		      .required(false)
		      .hasArg(false)	      
		      .desc("Help.")
		      .valueSeparator()
		      .build();
		
	   final Option serverListOption = Option.builder(SERVERLIST_OPTION_SHORT)
		      .required(false)
		      .hasArg()	      
		      .longOpt(SERVERLIST_OPTION_LONG)
		      .desc(String.format("Server list file. Cannot be used with %s option. Optional", SERVER_OPTION_SHORT))
		      .valueSeparator()
		      .build();
		
	   final Option dateListOption = Option.builder(DATELIST_OPTION_SHORT)
		      .required(false)
		      .hasArg()	      
		      .longOpt(DATELIST_OPTION_LONG)
		      .desc(String.format("Date list file. %s. Date format %s. Optional", DATE_OPTIONS, NMON2JSON.WHITELIST_DATE_PATTERN))
		      .valueSeparator()
		      .build();

	   final Option inputPathOption = Option.builder(INPUTPATH_OPTION_SHORT)
		   	  .required(false)
		      .hasArg()			      
		      .longOpt(INPUTPATH_OPTION_LONG)
		      .desc("Input path. Mandatory")
		      .valueSeparator()	      
		      .build();

	   final Option outputPathOption = Option.builder(OUTPUTPATH_OPTION_SHORT)
			  .required(false)
			  .hasArg()			      
			  .longOpt(OUTPUTPATH_OPTION_LONG)
			  .desc("Output path. Mandatory")
			  .valueSeparator()	      
			  .build();

	   final Option datacentreOption = Option.builder(DATACENTRE_OPTION_SHORT)
			  .required(false)
		      .hasArg()			      
		      .longOpt(DATACENTRE_OPTION_LONG)
		      .desc("Datacentre. Optional")
		      .valueSeparator()	      
		      .build();

	   final Option referenceOption = Option.builder(REFERENCE_OPTION_SHORT)
			  .required(false)
		      .hasArg()			      
		      .longOpt(REFERENCE_OPTION_LONG)
		      .desc("CSV file with infrastructure reference data. Optional")
		      .valueSeparator()	      
		      .build();
	   
	   final Option serverOption = Option.builder(SERVER_OPTION_SHORT)
			  .required(false)
		      .hasArg()			      
		      .longOpt(SERVER_OPTION_LONG)
		      .desc(String.format("Hostname to process. Cannot be used with %s option. Optional", SERVERLIST_OPTION_SHORT))
		      .valueSeparator()	      
		      .build();

	   final Option dateOption = Option.builder(DATE_OPTION_SHORT)
			  .required(false)
		      .hasArg()			      
		      .longOpt(DATE_OPTION_LONG)
		      .desc(String.format("Date to process. %s option. Date format %s. Optional", DATE_OPTIONS, NMON2JSON.WHITELIST_DATE_PATTERN))
		      .valueSeparator()	      
		      .build();
	
	   final Option relativeDateOption = Option.builder(RELATIVE_DATE_OPTION_SHORT)
				  .required(false)
			      .hasArg()			      
			      .longOpt(RELATIVE_DATE_OPTION_LONG)
			      .desc(String.format("Process a relative date from current date, substracting the parameter from the current date. %s. Date format %s. Optional", DATE_OPTIONS, NMON2JSON.WHITELIST_DATE_PATTERN))
			      .valueSeparator()	      
			      .build();
	   
	   final Option appendOption = Option.builder(APPEND_OPTION_SHORT)
			  .required(false)
		      .longOpt(APPEND_OPTION_LONG)
		      .desc("Append existing files. Optional")
		      .valueSeparator()	      
		      .build();
	   
	   final Option versionOption = Option.builder(VERSION_OPTION_SHORT)
			      .required(false)
			      .longOpt(VERSION_OPTION_LONG)
			      .hasArg(false)	      
			      .desc(String.format("Prints %s version. Does not process the command. Optional", NMON2JSON.APPPLICATION))
			      .valueSeparator()
			      .build();

	   final Option checkOption = Option.builder(CHECK_OPTION_SHORT)
			      .required(false)
			      .longOpt(CHECK_OPTION_LONG)
			      .hasArg(false)	      
			      .desc("Only check the arguments. Does not process the command. Optional")
			      .valueSeparator()
			      .build();
	   
	   final Options options = new Options();
	   
	   options.addOption(helpOption);
	   options.addOption(inputPathOption);
	   options.addOption(outputPathOption);
	   options.addOption(serverListOption);
	   options.addOption(dateListOption);
	   options.addOption(datacentreOption);	   
	   options.addOption(referenceOption);
	   options.addOption(serverOption);
	   options.addOption(dateOption);
	   options.addOption(relativeDateOption);
	   options.addOption(appendOption);
	   options.addOption(versionOption);
	   options.addOption(checkOption);
	   
	   return options;
	}	
	
	/**
	 * "Parsing" stage of command-line processing with
	 * Apache Commons CLI.
	 * @throws EPSMonioringException 
	 *
	 */
	private static CommandLine generateCommandLine(
	   final Options options, final String[] commandLineArguments) throws EPSMonioringException {
	   final CommandLineParser cmdLineParser = new DefaultParser();
	   CommandLine commandLine = null;
	   try {
	      commandLine = cmdLineParser.parse(options, commandLineArguments);
	   } catch (ParseException parseException) {
			throw new EPSMonioringException(
					"Unable to parse command-line arguments", 
					parseException, 
					ErrorCode.INVALID_ARGUMENTS);
	   }
	   return commandLine;
	}	
	
	/**
	 * Generate help information with Apache Commons CLI.
	 *
	 * @param options Instance of Options to be used to prepare
	 *    help formatter.
	 * @return HelpFormatter instance that can be used to print
	 *    help information.
	 */
	public static String printHelp() {
		
	   final HelpFormatter formatter = new HelpFormatter();
	   Options options = generateOptions();
	   
	   Writer sout = new StringWriter();
	   PrintWriter out = new PrintWriter(sout, true);
	   
	   final String usageHeader = NMON2JSON.APPPLICATION + " " + "Help";
	   final String usageFooter = "See Royal Mail for further details.";
	   out.println("\n====");
	   out.println("HELP");
	   out.println("====");
	   formatter.printHelp(out, 80, NMON2JSON.APPPLICATION, usageHeader, options, 2, 2, usageFooter);
	   out.flush();
	   out.close();
	   
	   return sout.toString();
	}	
	
	public boolean checkConfig(String... args) throws EPSMonioringException {
		Options options = generateOptions();
		
        if (args.length == 0) {
        	log.error("No arguments");
        	return false;
        }

        this.commandLine = generateCommandLine(options, args);
        
        if (commandLine == null) {
        	log.error("Error parsing the command line");
        	return false;
        }
        
// Process help option        
        if (commandLine.hasOption(ApplicationCLI.HELP_OPTION)) {
        	swHelp = (commandLine.hasOption(ApplicationCLI.HELP_OPTION));
        	log.info("Help argument");
        	return true;
        }

// Process version option        
        if (commandLine.hasOption(ApplicationCLI.VERSION_OPTION_LONG)) {
        	log.info("Version argument");
        	return true;
        }
        
// Additional option checks
        if (!checkMandatoryArguments(commandLine)) {
        	return false;
        }

// Process options        
        return processArguments(commandLine);
	}

	private boolean processArguments(CommandLine commandLine) {
		if (commandLine.hasOption(ApplicationCLI.SERVERLIST_OPTION_SHORT)) {
        	serverListFile = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.SERVERLIST_OPTION_SHORT));
        	if (serverListFile == null) {
        		return false;
        	}        	
        }
        
        if (commandLine.hasOption(ApplicationCLI.DATELIST_OPTION_SHORT)) {
        	dateListFile = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.DATELIST_OPTION_SHORT));
        	if (dateListFile == null) {
        		return false;
        	}
        }

        if (commandLine.hasOption(ApplicationCLI.INPUTPATH_OPTION_SHORT)) {
        	inputPath = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.INPUTPATH_OPTION_SHORT));
        	if (inputPath == null) {
        		return false;
        	}
        }

        if (commandLine.hasOption(ApplicationCLI.OUTPUTPATH_OPTION_SHORT)) {
        	outputPath = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.OUTPUTPATH_OPTION_SHORT));
        	if (outputPath == null) {
        		return false;
        	}
        }

        if (commandLine.hasOption(ApplicationCLI.DATACENTRE_OPTION_SHORT)) {
        	dataCentre = commandLine.getOptionValue(ApplicationCLI.DATACENTRE_OPTION_SHORT).toUpperCase();
        }
        
        if (commandLine.hasOption(ApplicationCLI.REFERENCE_OPTION_SHORT)) {
        	referenceCSVPath = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.REFERENCE_OPTION_SHORT));
        	if (referenceCSVPath == null) {
        		return false;
        	}        	
        } 
        
        if (commandLine.hasOption(ApplicationCLI.SERVER_OPTION_SHORT)) {
        	server = StringUtils.trim(commandLine.getOptionValue(ApplicationCLI.SERVER_OPTION_SHORT)).toUpperCase();
        } 

        if (commandLine.hasOption(ApplicationCLI.DATE_OPTION_SHORT)) {
			try {
				date = LocalDate.parse(StringUtils.trim(commandLine.getOptionValue(ApplicationCLI.DATE_OPTION_SHORT)), NMON2JSON.WHITELIST_DATE_FORMATTER);
			} catch (Exception e) {
				log.error("Argument {} is not a valid date with format {}", commandLine.getOptionValue(ApplicationCLI.DATE_OPTION_SHORT), NMON2JSON.WHITELIST_DATE_PATTERN);
				return false;
			}
        } 
        
        if (commandLine.hasOption(ApplicationCLI.RELATIVE_DATE_OPTION_SHORT)) {
			try {
				int relativeDate = Integer.parseUnsignedInt(StringUtils.trim(commandLine.getOptionValue(ApplicationCLI.RELATIVE_DATE_OPTION_SHORT)));
				date = LocalDate.now().minusDays(relativeDate);				
			} catch (Exception e) {
				log.error("Argument {} is not a valid date with format {}", commandLine.getOptionValue(ApplicationCLI.DATE_OPTION_SHORT), NMON2JSON.WHITELIST_DATE_PATTERN);
				return false;
			}
        } 
        
        swAppend = (commandLine.hasOption(ApplicationCLI.APPEND_OPTION_SHORT));
        return true;

	}

//	private Path string2Path(String pathSt) {
//		Path path = null;
//    	try {
//    		path = Paths.get(FilenameUtils.separatorsToUnix(pathSt));
//    	} catch (Exception e) {
//    		log.error("Argument {} is not a valid path", pathSt);
//		}		
//    	return path;
//	}
	
	private boolean checkMandatoryArguments(CommandLine commandLine) {
		if (!commandLine.hasOption(ApplicationCLI.INPUTPATH_OPTION_SHORT)) {
			log.error("Options {} is mandatory", ApplicationCLI.INPUTPATH_OPTION_SHORT);        	
        	return false;
        }
        
        if (!commandLine.hasOption(ApplicationCLI.OUTPUTPATH_OPTION_SHORT)) {
			log.error("Options {} is mandatory", ApplicationCLI.OUTPUTPATH_OPTION_SHORT);        	
        	return false;
        }
        
        if (commandLine.hasOption(ApplicationCLI.SERVERLIST_OPTION_SHORT) && commandLine.hasOption(ApplicationCLI.SERVER_OPTION_SHORT)) {
			log.error("Options {} and {} cannot be used at the same time", ApplicationCLI.SERVER_OPTION_SHORT, ApplicationCLI.SERVERLIST_OPTION_SHORT);
	    	return false;
        }

        // Check date options
        int dateOptions = (commandLine.hasOption(ApplicationCLI.DATELIST_OPTION_SHORT)?1:0) + (commandLine.hasOption(ApplicationCLI.DATE_OPTION_SHORT)?1:0)  + (commandLine.hasOption(ApplicationCLI.RELATIVE_DATE_OPTION_SHORT)?1:0);
        if (dateOptions > 1) {
    		log.error(DATE_OPTIONS);
        	return false;
        }
        return true;
	}
	
}
