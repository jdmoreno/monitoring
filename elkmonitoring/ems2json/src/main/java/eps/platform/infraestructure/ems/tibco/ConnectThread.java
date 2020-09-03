package eps.platform.infraestructure.ems.tibco;

import java.util.Map;
import java.util.Map.Entry;

import com.tibco.tibjms.admin.StateInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

import eps.platform.infraestructure.config.EmsConfiguration;
import eps.platform.infraestructure.config.EmsServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectThread implements Runnable {	
	private EmsConfiguration emsConfiguration = null;
	
	public ConnectThread(EmsConfiguration emsConfiguration) {	
		this.emsConfiguration = emsConfiguration; 
	}

	@Override
	public void run() {
		Map<String, EmsServer> servers = emsConfiguration.getServers();
		
		for (Entry<String, EmsServer> entry : servers.entrySet()) {
			EmsServer con = entry.getValue();
			TibjmsAdmin adminConn = null;
			try {
				if (con.getAdminConn() == null) {
	
					if (con.getUrl().startsWith("ssl")) {
						adminConn = new TibjmsAdmin(con.getUrl(), con.getUser(), con.getPassword(), con.getSslParams());
					} else {
						adminConn = new TibjmsAdmin(con.getUrl(), con.getUser(), con.getPassword());
					}
					adminConn.setCommandTimeout(5000);
	
					if (con.getStaleConn() != null) {
						// Clean up old stale connection
						try {
							con.getStaleConn().close();
							con.setStaleConn(null);
							log.debug("Cleaned up stale connection to server " + con.getAlias());
						} catch (TibjmsAdminException e1) {
							log.debug("Failed to clean up stale connection to server " + con.getAlias() + " " + e1.toString());
							con.setStaleConn(null);
						}
					}
	
					StateInfo stateInfo = adminConn.getStateInfo();
	
					if (stateInfo != null) {
						log.info("Connected to EMS " + (stateInfo.isAppliance() ? "Appliance: " : "Server: ")
								+ stateInfo.getServerName() + " V" + stateInfo.getVersionInfo().toString()
								+ " at " + con.getUrl() + " alias: " + con.getAlias());
	
						if (stateInfo != null && (stateInfo.getState().get()
								& com.tibco.tibjms.admin.State.SERVER_STATE_STANDBY) != 0) {
							// Connected to standby, try reconnecting to primary by swapping servers in URL
							String url = con.getUrl();
							String testString = con.getUrl();
							// We need to support 4 server URL, find number of , in URL to determine no of
							// attempts
							int attempts = testString.length() - testString.replace(",", "").length();
	
							for (int i = 0; i < attempts; i++) {
								int ci = url.indexOf(',');
								if (ci > 0) {
									adminConn.close();
									adminConn = null;
	
									String u = url.substring(ci + 1);
									u += ",";
									u += url.substring(0, ci);
									url = u;
	
									log.debug("connected to standby server " + con.getAlias()
											+ ", attempting to reconnect to active server at " + u);
									if (con.getUrl().startsWith("ssl"))
										adminConn = new TibjmsAdmin(u, con.getUser(), con.getPassword(),
												con.getSslParams());
									else
										adminConn = new TibjmsAdmin(u, con.getUser(), con.getPassword());
									adminConn.setCommandTimeout(5000);
									stateInfo = adminConn.getStateInfo();
									if (stateInfo != null && (stateInfo.getState().get()
											& com.tibco.tibjms.admin.State.SERVER_STATE_STANDBY) != 0) {
										log.debug("Still connected to standby server(" + u + "), retrying...");
									} else {
										log.debug("Connected to active server " + con.getAlias() + " at " + u);
										break;
									}
								}
							}
						}
					} else {
						log.warn("getSateInfo for " + con.getAlias() + " returned null, upgrade to EMS client 8.1 greater");
					}
	
					con.setAdminConn(adminConn);
				}
	
			} catch (TibjmsAdminException ex) {
				log.warn("Connect failed to Server " + con.getAlias() + " " + ex.toString());
				try {
					if (adminConn != null)
						adminConn.close();
				} catch (TibjmsAdminException e1) {
				}
			} catch (Exception ex1) {
				log.warn("Connect failed to Server " + con.getAlias() + " " + ex1.toString());
				try {
					if (adminConn != null)
						adminConn.close();
				} catch (TibjmsAdminException e1) {
				}
			} catch (Throwable th) {
				log.error("Connect failed to Server " + con.getAlias() + " " + th.toString());
			}
		}
	}
}
