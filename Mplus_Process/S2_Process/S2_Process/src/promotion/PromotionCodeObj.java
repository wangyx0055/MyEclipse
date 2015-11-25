package promotion;

public class PromotionCodeObj {

//	`ID` int(8) unsigned NOT NULL AUTO_INCREMENT,
//	  `SERVICES` varchar(100) NOT NULL,
//	  `PROMOTION_CODE` varchar(20) NOT NULL,
//	  `IS_USED`
	
	private int id = -1;
	private String services = "";
	private String promotionCode = "";
	private String userId = "";
	private int isUsed = 0;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getServices() {
		return services;
	}
	public void setServices(String services) {
		this.services = services;
	}
	public String getPromotionCode() {
		return promotionCode;
	}
	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(int isUsed) {
		this.isUsed = isUsed;
	}
	
	
	
}
