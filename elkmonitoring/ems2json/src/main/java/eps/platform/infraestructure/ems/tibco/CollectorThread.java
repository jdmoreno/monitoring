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

import eps.platform.infraestructure.common.EmsStatNames;
import eps.platform.infraestructure.config.EmsConfiguration;
import eps.platform.infraestructure.config.EmsServer;
import eps.platform.infraestructure.ems.stats.ListServersStats;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectorThread implements Runnable {

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

    private final BlockingQueue<ListServersStats> queue;
    private final EmsStatNames emsStats;
    private final EmsConfiguration emsConfiguration;
	
	public CollectorThread(BlockingQueue<ListServersStats> queue, EmsStatNames emsStats, EmsConfiguration emsConfiguration) {
		this.queue = queue;		
		this.emsStats = emsStats;
		this.emsConfiguration = emsConfiguration;
	}
	
	@Override	
	public void run() {

		long start = 0;
		long end = 0;

		ListServersStats statsCollection = new ListServersStats();
		
		Map<String, EmsServer> servers = emsConfiguration.getServers();
		start = System.nanoTime();
		
		for (Entry<String, EmsServer> entry : servers.entrySet()) {
			EmsServer emsServer = entry.getValue();
			if (emsServer.getAdminConn() != null) {
				long respTime = 0;

				try {
					Date timestamp = null;
					try {
						start = System.nanoTime();
						emsServer.setServerInfo(emsServer.getAdminConn().getInfo());

						end = System.nanoTime();
						timestamp = new Date();
						respTime = (end - start) / 1000000;
					} catch (TibjmsAdminSecurityException se) {
						emsServer.setServerInfo(null);
					}

					if (emsServer.getServerInfo() != null
							&& (emsServer.getServerInfo().getStateObj().get() & SERVER_STATE_ACTIVE) != 0) {
						statsCollection.getStatsServers().add(emsServer.getStatsValues(emsStats, timestamp, respTime));
					} else {
						log.warn("Server " + emsServer.getAlias()
								+ " in standby mode, logging will not start until server is active");
					}
				} catch (TibjmsAdminException ex) {
					if (ex.toString().contains("Timeout")) {
						if (start > 0) {
							end = System.nanoTime();
							respTime = (end - start) / 1000000;
						}
						// Clean up later..
						emsServer.setStaleConn(emsServer.getAdminConn());
						emsServer.close();
						serverDisconnected(emsServer, " timeout after " + respTime + "(ms) " + ex.toString());
					} else {
						emsServer.close();
						serverDisconnected(emsServer, ex.toString());
					}
				} catch (Exception ex1) {
					emsServer.close();
					serverDisconnected(emsServer, ex1.toString());
				}
			}
		}
		
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
