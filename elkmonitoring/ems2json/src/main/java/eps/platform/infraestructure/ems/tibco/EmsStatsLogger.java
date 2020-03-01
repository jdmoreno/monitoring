package eps.platform.infraestructure.ems.tibco;

import static com.tibco.tibjms.admin.State.SERVER_STATE_ACTIVE;

/* 
 * Based on 
 * EmsStatsLogger
 * Copyright (c) 2017-2018 TIBCO Software Inc.
 *
 * Uses EMS Admin API to collect server info stats and log them to CSV file.
 *
 * Compile with EMS client API V8.1 or greater; tibjmsadmin.jar
 */

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import com.tibco.tibjms.admin.TibjmsAdminException;
import com.tibco.tibjms.admin.TibjmsAdminSecurityException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmsStatsLogger implements Runnable {

	// Method properties from the EMS Admin API ServerInfo, DestinationInfo,
	// QueueInfo, TopicInfo, StatData classes that match the following statistics
	// naming patterns are automatically logged:
	// *MsgMem* *Rate *Size *Count *TotalMessages *TotalBytes
	//
	// However those properties that match the following names are excluded:
	// *Log* *Max* *SSL* *Trace* *MessagePool*
	//
	// In addition the methods listed below are also excluded.
	// Add any additional get methods here that you dont wish to be logged.

    private final BlockingQueue<StatsCollection> queue;
	
	public EmsStatsLogger(BlockingQueue<StatsCollection> queue) {
		this.queue = queue;		
	}
	
	@Override	
	public void run() {

		long start = 0;
		long end = 0;
//		long pollStart = 0;
//		long pollEnd = 0;

		StatsCollection statsCollection = new StatsCollection();
		
		Map<String, EmsServer> servers = EmsConfiguration.getServers();
		start = System.nanoTime();
		
//		pollStart = System.nanoTime();

		for (Entry<String, EmsServer> entry : servers.entrySet()) {
			EmsServer con = entry.getValue();
			if (con.getAdminConn() != null) {
				long respTime = 0;

				try {
					Date timestamp = null;
					try {
						start = System.nanoTime();
						con.setServerInfo(con.getAdminConn().getInfo());

						end = System.nanoTime();
						timestamp = new Date();
						respTime = (end - start) / 1000000;
					} catch (TibjmsAdminSecurityException se) {
						con.setServerInfo(null);
					}

					if (con.getServerInfo() != null
							&& (con.getServerInfo().getStateObj().get() & SERVER_STATE_ACTIVE) != 0) {
						statsCollection.getStatsServers().add(con.getStats(timestamp, respTime));
					} else {
						log.warn("Server " + con.getAlias()
								+ " in standby mode, logging will not start until server is active");
					}
				} catch (TibjmsAdminException ex) {
					if (ex.toString().contains("Timeout")) {
						if (start > 0) {
							end = System.nanoTime();
							respTime = (end - start) / 1000000;
						}
						// Clean up later..
						con.setStaleConn(con.getAdminConn());
						con.close();
						serverDisconnected(con, " timeout after " + respTime + "(ms) " + ex.toString());
					} else {
						con.close();
						serverDisconnected(con, ex.toString());
					}
				} catch (Exception ex1) {
					con.close();
					serverDisconnected(con, ex1.toString());
				}
			}
		}
//		pollEnd = System.nanoTime();
		
		try {
			queue.put(statsCollection);
		} catch (InterruptedException e) {
			log.error("Error writing stats to the queue");
		}
	}

	public void serverDisconnected(EmsServer con, String reason) {
		log.warn("Disconnected from Server " + con.getAlias() + " " + reason);
	}
}
