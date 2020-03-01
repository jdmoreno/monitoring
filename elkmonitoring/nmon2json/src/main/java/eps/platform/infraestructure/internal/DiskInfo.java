package eps.platform.infraestructure.internal;

import java.util.HashMap;
import java.util.Map;

import eps.platform.infraestructure.common.Utils;
import lombok.Getter;
import lombok.ToString;

@ToString
public class DiskInfo {
	@Getter private Map <String, DiskDetailInfo> diskDetailInfos = new HashMap<>();
	
	public void recordDISKBUSY(String[] diskHeader, String[] tokens) {
		recordDISK(DiskInfos.DISK_BUSY, diskHeader, tokens);
	}
	
	public void recordDISKREAD(String[] diskHeader, String[] tokens) {
		recordDISK(DiskInfos.DISK_READ, diskHeader, tokens);
	}

	public void recordDISKWRITE(String[] diskHeader, String[] tokens) {
		recordDISK(DiskInfos.DISK_WRITE, diskHeader, tokens);
	}

	public void recordDISKXFER(String[] diskHeader, String[] tokens) {
		recordDISK(DiskInfos.DISK_XFER, diskHeader, tokens);
	}

	public void recordDISKBSIZE(String[] diskHeader, String[] tokens) {
		recordDISK(DiskInfos.DISK_BSIZE, diskHeader, tokens);
	}	
	
	private void recordDISK(DiskInfos diskInfo, String[] diskHeader, String[] tokens) {
		for (int i = 2; i < diskHeader.length; i++) {
			if (i < tokens.length) {
				String diskId = diskHeader[i];
				double value = Double.parseDouble(Utils.returnValueOrZero(tokens[i]));

				DiskDetailInfo diskDetailInfo = this.diskDetailInfos.get(diskId);
				if (diskDetailInfo == null) {
					diskDetailInfo = new DiskDetailInfo(diskId);
					diskDetailInfos.put(diskId, diskDetailInfo);
				}
				switch (diskInfo) {
				case DISK_BSIZE:
					diskDetailInfo.setDiskBsize(value);
					break;
	
				case DISK_BUSY:
					diskDetailInfo.setDiskBusy(value);
					break;
					
				case DISK_READ:
					diskDetailInfo.setDiskRead(value);
					break;
	
				case DISK_WRITE:
					diskDetailInfo.setDiskWrite(value);
					break;
	
				case DISK_XFER:
					diskDetailInfo.setDiskXfer(value);
					break;
				}
			}
		}
	}
	
	private enum DiskInfos {
		DISK_BUSY, DISK_READ, DISK_WRITE, DISK_XFER, DISK_BSIZE
	}
}
