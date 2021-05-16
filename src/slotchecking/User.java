package slotchecking;

public class User {

	private String name;
	private String gcmKey;
	private String gcmToken;
	private int pin;
	private int districtId;
	private String districtName;
	private int pushCount;
	private int minAge;
	private int minCapacity;
	private boolean anyVaccinePreferred;
	private String preferredVaccine;
	private boolean notifyOnlyWhenSlotInPin;
	private  String lastNotificationSent;
	private long lastNotificationTime;
	private int maxNoOfTimesSamePushSend = 1;
	private  int lastSamePushCount;
	private  boolean dose1Preferred;
	private  boolean dose2Preferred;

	public boolean isDose1Preferred() {
		return dose1Preferred;
	}

	public void setDose1Preferred(boolean dose1Preferred) {
		this.dose1Preferred = dose1Preferred;
	}

	public boolean isDose2Preferred() {
		return dose2Preferred;
	}

	public void setDose2Preferred(boolean dose2Preferred) {
		this.dose2Preferred = dose2Preferred;
	}

	public int getLastSamePushCount() {
		return lastSamePushCount;
	}

	public void setLastSamePushCount(int lastSamePushCount) {
		this.lastSamePushCount = lastSamePushCount;
	}

	public int getMaxNoOfTimesSamePushSend() {
		return maxNoOfTimesSamePushSend;
	}

	public void setMaxNoOfTimesSamePushSend(int maxNoOfTimesSamePushSend) {
		this.maxNoOfTimesSamePushSend = maxNoOfTimesSamePushSend;
	}

	public String getLastNotificationSent() {
		return lastNotificationSent;
	}

	public void setLastNotificationSent(String lastNotificationSent) {
		this.lastNotificationSent = lastNotificationSent;
	}

	public long getLastNotificationTime() {
		return lastNotificationTime;
	}

	public void setLastNotificationTime(long lastNotificationTime) {
		this.lastNotificationTime = lastNotificationTime;
	}

	public boolean isNotifyOnlyWhenSlotInPin() {
		return notifyOnlyWhenSlotInPin;
	}

	public void setNotifyOnlyWhenSlotInPin(boolean notifyOnlyWhenSlotInPin) {
		this.notifyOnlyWhenSlotInPin = notifyOnlyWhenSlotInPin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGcmKey() {
		return gcmKey;
	}

	public void setGcmKey(String gcmKey) {
		this.gcmKey = gcmKey;
	}

	public String getGcmToken() {
		return gcmToken;
	}

	public void setGcmToken(String gcmToken) {
		this.gcmToken = gcmToken;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public int getPushCount() {
		return pushCount;
	}

	public void setPushCount(int pushCount) {
		this.pushCount = pushCount;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMinCapacity() {
		return minCapacity;
	}

	public void setMinCapacity(int minCapacity) {
		this.minCapacity = minCapacity;
	}

	// does the user have vaccine preference the  isAnyVaccinePreferred return true
	public boolean isAnyVaccinePreferred() {
		return anyVaccinePreferred;
	}

	public void setAnyVaccinePreferred(boolean anyVaccinePreferred) {
		this.anyVaccinePreferred = anyVaccinePreferred;
	}

	public String getPreferredVaccine() {
		return preferredVaccine;
	}

	public void setPreferredVaccine(String preferredVaccine) {
		this.preferredVaccine = preferredVaccine;
	}


	public String getUserDetails() {
		return
				"name:" + name + 
				"\npin: " + pin + 
				"\ndistrictName: " +  districtName +
				"\nminAge: " + minAge +
				"\ndose1Preferred: " + dose1Preferred +
				"\ndose2Preferred: " + dose2Preferred +
				"\npreferredVaccine: " + preferredVaccine +
				"\nnotifyOnlyWhenSlotInPin: " + notifyOnlyWhenSlotInPin +
				"\n";
	}
	
	@Override
	public String toString() {
		return
				"\nname:" + name + 
				"\npin: " + pin + 
				"\ndistrictId: " + districtId +
				"\ndistrictName: " +  districtName +
				"\ndose1Preferred: " + dose1Preferred +
				"\ndose2Preferred: " + dose2Preferred +
				"\nminAge: " + minAge +
				"\nminCapacity: " + minCapacity +
				"\nanyVaccinePreferred: " + anyVaccinePreferred +
				"\npreferredVaccine: " + preferredVaccine +
				"\nnotifyOnlyWhenSlotInPin: " + notifyOnlyWhenSlotInPin +
				"\nlastNotificationSent: " + lastNotificationSent +
				"\nlastNotificationTime: " + lastNotificationTime +
				"\nlastSamePushCount: " + lastSamePushCount +
				"\npushCount: " + pushCount +
				"\n";
	}
}