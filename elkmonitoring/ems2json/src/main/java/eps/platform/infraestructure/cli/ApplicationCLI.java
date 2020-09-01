package eps.platform.infraestructure.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import eps.platform.infraestructure.EMS2JSON;
import eps.platform.infraestructure.common.Utils;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j 
public class ApplicationCLI {
	
	private static final String HELP_OPTION_LONG = "help";
	private static final String HELP_OPTION_SHORT = "h";

	private static final String SEVERS_FILE_PATH_OPTION_LONG = "servers";
	private static final String SEVERS_FILE_OPTION_SHORT = "s";
	
	private static final String INTERVAL_OPTION_LONG = "interval";
	private static final String INTERVAL_OPTION_SHORT = "f";
	
//	private static final String DEBUG_OPTION_LONG = "debug";
//	private static final String DEBUG_OPTION_SHORT = "d";
	
	private static final String VERSION_OPTION_LONG = "version";
	private static final String VERSION_OPTION_SHORT = "v";

	private static final String CHECK_OPTION_LONG = "check";
	private static final String CHECK_OPTION_SHORT = "ch";
	
	private static final String REFERENCE_OPTION_LONG = "reference";
	private static final String REFERENCE_OPTION_SHORT = "r";

	private static final String CSV_OPTION_SHORT = "csv";
	
	@Getter private boolean swHelp = false;
	@Getter private boolean swDebug = false;
	@Getter private boolean swCSV = false;
	
	@Getter private Path serversFilePath = null;
	@Getter private Path outputPath = null;
	@Getter private int interval = 0;	
	@Getter private Path referenceCSVPath = null;

	/**
	 * "Definition" stage of command-line parsing with Apache Commons CLI.
	 * @return Definition of command-line options.
	 */	
	private static Options generateOptions() {

	   final Option helpOption = Option.builder(HELP_OPTION_SHORT)
		      .required(false)
		      .hasArg(false)
		      .longOpt(HELP_OPTION_LONG)
		      .desc("Help.")
		      .valueSeparator()
		      .build();
		
	   final Option serversFilePathOption = Option.builder(SEVERS_FILE_OPTION_SHORT)
		   	  .required(false)
		      .hasArg()			      
		      .longOpt(SEVERS_FILE_PATH_OPTION_LONG)
		      .desc("Servers yaml configuration file. Mandatory")
		      .valueSeparator()	      
		      .build();

	   final Option intervalOption = Option.builder(INTERVAL_OPTION_SHORT)
				  .required(false)
				  .hasArg()
			      .longOpt(INTERVAL_OPTION_LONG)
			      .desc("Polling interval. Mandatory")
			      .valueSeparator()	      
			      .build();
	   
	   final Option referenceOption = Option.builder(REFERENCE_OPTION_SHORT)
				  .required(false)
			      .hasArg()			      
			      .longOpt(REFERENCE_OPTION_LONG)
			      .desc("CSV file with infrastructure reference data. Optional")
			      .valueSeparator()	      
			      .build();
	   
	   final Option versionOption = Option.builder(VERSION_OPTION_SHORT)
			      .required(false)
			      .longOpt(VERSION_OPTION_LONG)
			      .hasArg(false)	      
			      .desc(String.format("Prints %s version. Does not process the command. Optional", EMS2JSON.APPPLICATION))
			      .valueSeparator()
			      .build();
	   
	   final Option checkOption = Option.builder(CHECK_OPTION_SHORT)
			      .required(false)
			      .longOpt(CHECK_OPTION_LONG)
			      .hasArg(false)	      
			      .desc("Only check the arguments. Does not process the command. Optional")
			      .valueSeparator()
			      .build();

	   final Option csvOption = Option.builder(CSV_OPTION_SHORT)
			      .required(false)
			      .hasArg(false)	      
			      .desc("Generate CSV output instead of JSON. Optional")
			      .valueSeparator()
			      .build();
	   
	   final Options options = new Options();
	   
	   options.addOption(helpOption);
	   options.addOption(serversFilePathOption);
	   options.addOption(referenceOption);	   
	   options.addOption(intervalOption);
	   options.addOption(versionOption);
	   options.addOption(checkOption);
	   options.addOption(csvOption);
	   
	   return options;
	}	
	
	/**
	 * "Parsing" stage of command-line processing with
	 * Apache Commons CLI.
	 *
	 */
	private static CommandLine generateCommandLine(
	   final Options options, final String[] commandLineArguments) {
	   final CommandLineParser cmdLineParser = new DefaultParser();
	   CommandLine commandLine = null;
	   try {
	      commandLine = cmdLineParser.parse(options, commandLineArguments);
	   } catch (ParseException parseException) {
		  String errorMessage = "ERROR: Unable to parse command-line arguments "
	         + Arrays.toString(commandLineArguments) + " due to: "
			 + parseException;
		  System.err.println(errorMessage);
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
	   
	   final String usageHeader = EMS2JSON.APPPLICATION + " " + "Help";
	   final String usageFooter = "See Royal Mail for further details.";
	   out.println("\n====");
	   out.println("HELP");
	   out.println("====");
	   formatter.printHelp(out, 80, EMS2JSON.APPPLICATION, usageHeader, options, 2, 2, usageFooter);
	   out.flush();
	   out.close();
	   
	   return sout.toString();
	}	
	
	public boolean checkConfig(String... args) {
		Options options = generateOptions();
		
        if (args.length == 0) {
        	log.error("No arguments");
        	return false;
        }

        CommandLine commandLine = generateCommandLine(options, args);
        
        if (commandLine == null) {
        	log.error("Error parsing the command line");
        	return false;
        }
        
// Process help option
        swHelp = (commandLine.hasOption(ApplicationCLI.HELP_OPTION_SHORT));
        if (swHelp) {
        	log.info("Help argument");
        	return true;
        }
        
// Process CSV option
        swCSV = (commandLine.hasOption(ApplicationCLI.CSV_OPTION_SHORT));
        if (swCSV) {
        	log.info("CSV argument");
        }
        
// Additional option checks
        if (!commandLine.hasOption(ApplicationCLI.SEVERS_FILE_OPTION_SHORT)) {
			log.error("Options {} is mandatory", ApplicationCLI.SEVERS_FILE_OPTION_SHORT);        	
        	return false;
        }
        
        if (commandLine.hasOption(ApplicationCLI.INTERVAL_OPTION_SHORT)) {
			try {
				interval = Integer.parseUnsignedInt(StringUtils.trim(commandLine.getOptionValue(ApplicationCLI.INTERVAL_OPTION_SHORT)));
			} catch (Exception e) {
				log.error("Argument {} is not a valid numeric", commandLine.getOptionValue(ApplicationCLI.INTERVAL_OPTION_SHORT));
				return false;
			}
        } else {
			log.error("Options {} is mandatory", ApplicationCLI.INTERVAL_OPTION_SHORT);        	
        	return false;        	
        }
        
        if (commandLine.hasOption(ApplicationCLI.REFERENCE_OPTION_SHORT)) {
        	referenceCSVPath = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.REFERENCE_OPTION_SHORT));
        	if (referenceCSVPath == null) {
        		return false;
        	}        	
        } 
        
// Process options        
        if (commandLine.hasOption(ApplicationCLI.SEVERS_FILE_OPTION_SHORT)) {
        	serversFilePath = Utils.string2Path(commandLine.getOptionValue(ApplicationCLI.SEVERS_FILE_OPTION_SHORT));
        }

		return true;
	}
	
}
