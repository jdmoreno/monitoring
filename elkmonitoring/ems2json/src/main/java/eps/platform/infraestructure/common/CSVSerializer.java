package eps.platform.infraestructure.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;

import eps.platform.infraestructure.dto.ems.DestinationInfoDto;
import eps.platform.infraestructure.dto.ems.ServerInfoDto;
import eps.platform.infraestructure.dto.nmon.HeaderDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVSerializer {
	public static void serialize(ServerInfoDto info, List<String> headers, final Appendable out) throws IOException {

		// Order values according to name as the CSV header
		Map<String, Object> namesValues = new HashMap<>();

		namesValues.put(Common.CSV_LIT_RECORD_TYPE, info.getType());
		
		serialize(namesValues, info.getHeader());
		
		namesValues.put(Common.CSV_LIT_EMS_SERVER_NAME, info.getServerName());
		namesValues.put(Common.CSV_LIT_EMS_HOSTNAME, info.getHostname());
		namesValues.put(Common.CSV_LIT_URL, info.getUrl());
		
		
		for (Entry<String, Object> entry : info.getStats().entrySet()) {
			if (namesValues.get(entry.getKey()) == null) {
				namesValues.put(entry.getKey(), entry.getValue());
			} else {
				log.warn("Duplicate metric {} for server {}", entry.getKey(), info.getServerName());
			}
		}
		
		serializeRow(info.getServerName(), headers, out, namesValues);
	}

	public static void serialize(DestinationInfoDto info, List<String> headers, final Appendable out) throws IOException {
		
		// Order values according to name as the CSV header
		Map<String, Object> namesValues = new HashMap<>();

		namesValues.put(Common.CSV_LIT_RECORD_TYPE, info.getType());
		
		serialize(namesValues, info.getHeader());

		namesValues.put(Common.CSV_LIT_EMS_SERVER_NAME, info.getServerName());
		namesValues.put(Common.CSV_LIT_EMS_HOSTNAME, info.getHostname());
		
		for (Entry<String, Object> entry : info.getStats().entrySet()) {
			if (namesValues.get(entry.getKey()) == null) {
				namesValues.put(entry.getKey(), entry.getValue());
			} else {
				log.warn("Duplicate metric {} for server {}", entry.getKey(), info.getServerName());
			}
		}

		for (Entry<String, Object> entry : info.getStatsInbound().entrySet()) {
			if (namesValues.get(entry.getKey()) == null) {
				namesValues.put(entry.getKey(), entry.getValue());
			} else {
				log.warn("Duplicate metric {} for server {}", entry.getKey(), info.getServerName());
			}			
		}

		for (Entry<String, Object> entry : info.getStatsOutbound().entrySet()) {
			if (namesValues.get(entry.getKey()) == null) {
				namesValues.put(entry.getKey(), entry.getValue());
			} else {
				log.warn("Duplicate metric {} for server {}", entry.getKey(), info.getServerName());
			}						
		}
		
		serializeRow(info.getServerName(), headers, out, namesValues);		
	}
	
	private static void serializeRow(String serverName, List<String> headers, final Appendable out,
			Map<String, Object> namesValues) throws IOException {
		Object value;
		boolean swFirst = true;
		for (String header : headers) {
			value = namesValues.get(header);
			if (value != null) {
				CSVFormat.DEFAULT.print(value, out, swFirst);
				if (swFirst) {
					swFirst = false;	
				}
			} else {
				log.warn("Header \"{}\" not found in stats for server \"{}\"", header, serverName);
			}
		}
	}
	
	public static void serialize(Map<String, Object> namesValues, HeaderDto info) throws IOException {
		namesValues.put(Common.CSV_LIT_SOURCE, info.getSource());
		namesValues.put(Common.CSV_LIT_SECTION, info.getSection());
		namesValues.put(Common.CSV_LIT_TIMESTAMP, info.getTimestamp());
		namesValues.put(Common.CSV_LIT_HOSTNAME, info.getHostname());
		namesValues.put(Common.CSV_LIT_OS, info.getOs());
		namesValues.put(Common.CSV_LIT_DATACENTRE, info.getHostname());
		namesValues.put(Common.CSV_LIT_ENVIRONMENT, info.getEnvironment());
		namesValues.put(Common.CSV_LIT_SERVER_FUNCTION, info.getServerFunction());
	}
	
	public static <T> String iterableToCSV(Iterable<T> literals) {
		StringBuilder out = new StringBuilder(); 
		boolean swFirst = true;
		for (T literal : literals) {
			try {
				CSVFormat.DEFAULT.print(literal.toString(), out, swFirst);
				if (swFirst) swFirst = false;
			} catch (IOException e) {
				log.error(e.getClass() + ": " + e.getMessage());
			}
		}
		return out.toString();
	}
}
