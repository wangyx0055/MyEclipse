package daugia;

public class DGConstants {

	public static String DG_POOL_NAME = "mplusdaugia";
	//
	public static String DG_SERVICE_NAME = "DAUGIA";
	// Table Name
	public static String TABLE_MLIST_DG = "mlist_daugia_icom";
	public static String TABLE_MLIST_DG_TMP = "daugia_notregistry";
	public static String TABLE_DG_CHARGE = "vms_dg_charge";
	public static String TABLE_DG_CHARGE_RESULT = "vms_dg_charge_result";
	public static String TABLE_DG_AMOUNT = "daugia_amount";
	public static String TABLE_DG_SANPHAM = "daugia_sanpham";
	public static String TABLE_DG_TRUNGTHUONG = "daugia_trungthuong";

	// Number Thread Handle Result::
	public static int DG_PROCESS_RESULTS_NUM = 10;

	public static int TYPE_WIN_TMP_FIRST_TIME = 0; // win template ngay lan nhan
	// tin dau
	public static int TYPE_WIN_TMP_LAST = 1; // win template do nguoi truoc do
	// mat uu the.

	public static int WIN_RANK_FIRST = 1;
	public static int WIN_RANK_SECOND = 2;
	public static int WIN_RANK_THIRD = 3;

	public static int DG_TIME_RESET = 0;
	// MODE TEST

	public static int DG_MODE = 1; // 0: Testing, 1: is running

	// BLACK LIST
	public static int DAUGIA_DURATION_TIME_CHECK_BLACK_LIST = 1;

	public static int DAUGIA_NUMBERMO_INVALID = 1000;

	public static String MTQUEUE_PUSH = "mt_queue";
	public static String MTQUEUE_DKHUY = "mt_queue";
	public static String MTQUEUE_WIN = "mt_queue";

	public static int CONTENT_TYPE_PUSH_MANUAL = 1;
	public static int CONTENT_TYPE_PUSH_DAILY = 0;

	public static int NUMBER_REGCOUNT = 5;

	public static String TIME_WIN_DAILY = "19:50";

	// MT Return
	public static String MTINFO_MIN_AND_ONLY_1 = "Chuc mung ban. Muc gia "
			+ "DG_AMOUNT ma ban dat dang la thap nhat va duy nhat. "
			+ "Hay dat them cac muc gia khac de nang cao kha nang gianh chien thang."
			+ " Soan tin: DG [gia dau] gui 9209";

	public static String MTINFO_MIN_AND_ONLY = "Muc gia DG_AMOUNT ma ban dat dang la thap nhat va duy nhat"
			+ " co co hoi so huu TEN_SP. "
			+ "Hay dat them cac muc gia khac de nang cao kha nang gianh chien thang."
			+ " Soan tin: DG [gia dau] gui 9209";

	/*
	 * public static String MTINFO_MIN_AND_NO_ONLY =
	 * "Ban da dat gia la DG_AMOUNT." +
	 * "Day la muc gia thap nhat nhung khong phai la duy nhat." +
	 * "Nhanh tay dat gia khac soan tin: DG [gia dau] gui 9209 de gianh co hoi gianh duoc TEN_SP."
	 * + "khoang gia MIN_EMPTY - MAX_EMPTY co nhieu gia chua ai dat";
	 */
	public static String MTINFO_MIN_AND_NO_ONLY = "Ban da dat gia la DG_AMOUNT."
			+ " Day la muc gia thap nhat nhung khong phai la duy nhat. "
			+ "Goi y! de co co hoi chien thang ban nen dat gia trong khoang MIN_EMPTY - MAX_EMPTY."
			+ " Soan tin: DG [gia dau] gui 9209 de gianh co hoi gianh duoc TEN_SP.";

	public static String MTINFO_MIN_AND_NO_ONLY_EMPTYNUMBER = "Ban da dat gia la DG_AMOUNT."
			+ "Day la muc gia thap nhat nhung khong phai la duy nhat."
			+ " Khoang gia MIN_EMPTY - MAX_EMPTY con nhieu muc gia duy nhat chua ai dat."
			+ " Nhanh tay dat gia khac de co co hoi so huu san pham TEN_SP."
			+ " Soan tin DK gui 9209 de duoc ho tro ve cu phap cac"
			+ " dich vu khac cua mPlus. Cam on ban da su dung dich vu cua MobiFone."
			+ " DTHT 9244. Chi tiet tai http://m.mplus.vn";

	public static String MTINFO_NO_MIN_AND_ONLY = "Ban da dat gia la DG_AMOUNT."
			+ "Day la muc gia duy nhat nhung khong phai la thap nhat."
			+ "Nhanh tay dat gia khac soan tin: DG [gia dau] gui 9209 de gianh co hoi gianh duoc TEN_SP."
			+ "khoang gia MIN_EMPTY - MAX_EMPTY co nhieu gia chua ai dat";
	public static String MTINFO_NO_MIN_AND_ONLY_TOP20 = "Chuc mung ban gia DG_AMOUNT ban vua dat la gia Duy nhat!"
			+ " Phan doan cua ban rat tot, "
			+ "day la 1 trong 20 gia co co hoi can chien thang. "
			+ "Hay dat them gia khac de nang cao kha nang chien thang, "
			+ "soan tin: DG [gia dau] gui 9209.";
	public static String MTINFO_NO_MIN_AND_ONLY_NEAR = "Chuc mung ban! "
			+ "Gia DG_AMOUNT ban vua dat la gia Duy nhat!"
			+ " Muc gia thap nhat va duy nhat hien tai dang thap hon muc gia ban dua ra."
			+ " Hay dat them gia khac de nang cao kha nang chien thang,"
			+ " soan tin: DG [gia dau] gui 9209.";
	public static String MTINFO_NO_MIN_AND_ONLY_FAR = "That tuyet! gia DG_AMOUNT ban vua dat la gia Duy nhat!"
			+ " Khoang MIN_EMPTY - MAX_EMPTY con nhieu muc gia chua ai dat va co kha nang chien thang cao hon."
			+ " Hay dat them gia khac de nang cao kha nang chien thang,"
			+ " soan tin: DG [gia dau] gui 9209.";
	public static String MTINFO_NO_MIN_AND_ONLY_NUMBERUNDER = "Ban da dat gia la DG_AMOUNT."
			+ "Day la gia duy nhat nhung chua phai thap nhat. "
			+ "Duoi muc gia nay con NUMBER_UNDER muc gia chua ai dat. "
			+ "Nhanh tay dat gia khac de tang co hoi so huu san pham TEN_SP."
			+ "Soan tin DK gui 9209 de duoc ho tro ve cu phap cac dich vu "
			+ "khac cua mPlus.Cam on ban da su dung dich vu cua MobiFone."
			+ "DTHT 9244.Chi tiet tai http://m.mplus.vn";

	/*
	 * public static String MTINFO_NO_MIN_AND_NO_ONLY =
	 * "Ban da dat gia la DG_AMOUNT." +
	 * "Day khong phai la gia thap nhat va duy nhat. Nhanh tay dat gia khac soan tin: "
	 * + "DG [gia dau] gui 9209 de gianh co hoi gianh duoc [san pham], " +
	 * "khoang gia MIN_EMPTY - MAX_EMPTY con nhieu muc gia chua ai dat";
	 */
	public static String MTINFO_NO_MIN_AND_NO_ONLY = "Ban da dat gia la DG_AMOUNT day khong phai la gia "
			+ "Thap nhat va Duy nhat."
			+ " soan tin: DG [gia dau] gui 9209 de gianh co hoi gianh duoc TEN_SP,"
			+ " khoang gia MIN_EMPTY - MAX_EMPTY dang chiem uu the.";

	public static String MTINFO_NO_MIN_AND_NO_ONLY_EMPTYNUMBER = "Ban da dat gia la DG_AMOUNT."
			+ "Day khong phai la gia thap nhat va duy nhat."
			+ "Khoang gia MIN_EMPTY - MAX_EMPTY con nhieu muc gia chua ai dat."
			+ "Nhanh tay dat gia "
			+ "khac de gianh chien thang va so huu san pham TEN_SP."
			+ "Soan tin DK gui 9209 de duoc ho tro ve cu phap cac dich vu "
			+ "khac cua mPlus.Cam on ban da su dung dich vu cua MobiFone."
			+ "DTHT 9244.Chi tiet tai http://m.mplus.vn";

	public static String MTINFO_NO_WIN = "Phien dau gia tuan nay da ket thuc."
			+ " Xin chuc mung thue bao USER_ID, "
			+ "nguoi da tra gia DG_AMOUNT va gianh chien thang, "
			+ "so huu san pham TEN_SP. Lien he 9244 hoac truy cap http://m.mplus.vn de biet them thong tin chi tiet."
			+ " Cam on ban da su dung dich vu cua MobiFone "
			+ "Chuc ban may man o nhung phien dau gia tiep theo";

	public static String MTINFO_NO_CUS_WIN = "San pham dau gia TEN_SP trong phien dau gia "
			+ "ngay START_DATE "
			+ "khong co khach hang tra gia thap nhat va duy nhat. "
			+ "Chuc ban may man trong phien dau gia toi.Soan tin DK gui "
			+ "9209 de duoc ho tro ve cu phap cac dich vu khac cua mPlus."
			+ "Cam on ban da su dung dich vu cua Mobifone.DTHT 9244."
			+ "Chi tiet tai http://m.mplus.vn";

	public static String MTINFO_WIN = "Phien dau gia tuan nay da ket thuc. "
			+ "Xin chuc mung thue bao USER_ID, "
			+ "ban da tra gia DG_AMOUNT va gianh chien thang,"
			+ " so huu san pham TEN_SP."
			+ " Lien he 9244 hoac truy cap http://m.mplus.vn de biet them thong tin chi tiet."
			+ " Cam on ban da su dung dich vu cua MobiFone";

	public static String MTINFO_DAILY = "Muc gia dang chiem uu the"
			+ " nam trong khoang AMOUNT_MIN den AMOUNT_MAX, va thuoc ve"
			+ " thue bao USER_ID. Chi can dat gia thap nhat "
			+ "va duy nhat la ban se so huu san pham. "
			+ "Nhanh tay soan DG <muc gia> gui 9209."
			+ "Soan tin DK gui 9209 de duoc ho tro ve cu phap cac "
			+ "dich vu khac cua mPlus.Cam on ban da su dung dich vu "
			+ "cua MobiFone.DTHT 9244.Chi tiet tai http://m.mplus.vn";

	public static String MTINFO_DAILY_NO_WIN = "Trong thoi diem hien tai chua co thue bao "
			+ "chiem uu the la nguoi tra gia thap nhat va duy nhat. "
			+ "Nhanh tay soan tin de tang co hoi mua san pham voi gia "
			+ "khong tuong.Soan tin DK gui 9209 de duoc ho tro ve cu "
			+ "phap cac dich vu khac cua mPlus.Cam on ban da su dung "
			+ "dich vu cua Mobifone.DTHT 9244.Chi tiet tai "
			+ "http://m.mplus.vn";

	public static String MTINFO_START_DAUGIA = "San pham dau gia tuan nay la "
			+ "TEN_SP co gia GIA_SP. Phien dau gia chinh se ket thuc vao 19h50 ngay"
			+ " END_DATE. Nhanh tay soan DG [gia dau] gui 9209 de la "
			+ "nguoi chien thang trong ngay va co co hoi so huu "
			+ " TEN_SP voi dieu kien ban la nguoi tra gia THAP NHAT va DUY NHAT."
			+ " VD: Ban tra gia SP la 25000d, soan DG 25 gui 9209";
	// + "Gia ban tra la boi so cua 1000";

	public static String MTINFO_WRONG_AMOUNT = "Ban luu y muc gia phai la so nguyen,"
			+ " VD: Ban tra gia SP la 25000d.Soan DG 25 gui 9209. Gia ban tra la boi so 1000";
	// + " Gia ban tra la boi so 1000";

	public static String MTINFO_WRONG_KEYWORD = "Tin nhan chua dung"
			+ " cu phap.Soan tin DG [muc gia] gui 9209 de la chu"
			+ " nhan cua nhung san pham that hot voi dieu kien"
			+ " ban la nguoi tra gia thap nhat va duy nhat."
			+ " VD: Ban tra gia SP la 25000d.Soan DG 25 gui 9209. ";

	public static String MT_ALERT_2 = "Chi con 30ph nua de ban tra gia"
			+ " va gianh chien thang phien dau gia tuan nay voi san pham"
			+ " TEN_SP (gia thi truong la GIA_SP). Muc gia dang chiem uu"
			+ " the nam trong khoang AMOUNT_MIN den AMOUNT_MAX, thuoc ve thue bao"
			+ " USER_ID .Phien dau gia se ket thuc vao 14h ngay END_DATE."
			+ " Dung chan chu nua, ban hay soan ngay DG <muc gia> gui 9209,"
			+ " san pham TEN_SP dang cho ban so huu neu muc gia ban tra"
			+ " la thap nhat va duy nhat. VD: Ban tra gia SP la 25000d."
			+ " Soan DG 25 gui 9209.";
	// + " Gia ban tra la boi so 1000";

	public static String MT_ALERT_1 = "Chi gan 2h nua de tim ra nguoi"
			+ " chien thang cua tuan nay. San pham dau gia tuan nay la"
			+ " TEN_SP co gia GIA_SP. Phien dau gia se ket thuc vao"
			+ " END_HOUR ngay END_DATE. Muc gia dang chiem uu"
			+ " the nam trong khoang AMOUNT_MIN den AMOUNT_MAX,"
			+ " thuoc ve thue bao USER_ID."
			+ " Hay nhanh tay nam bat co hoi, soan DG <muc gia> gui 9209"
			+ " va so huu san pham TEN_SP neu gia ban tra la"
			+ " thap nhat va duy nhat";

	public static String MT_SAME_AMOUNT = "Da co nguoi tra gia GIA_SP "
			+ "trung voi gia ban tra, ban khong con la nguoi chiem gia thap nhat va duy nhat nua."
			+ " Nhanh tay soan: DG [gia dau] gui 9209 de dat cac muc"
			+ " gia khac de co co hoi so huu TEN_SP";

	public static String MT_UNDER_YOUR_AMOUNT = "Da co nguoi tra gia"
			+ " thap hon gia GIA_SP cua ban. Ban khong con la nguoi"
			+ " chiem gia thap nhat va duy nhat nua."
			+ " Nhanh tay dat cac muc gia khac de co co hoi "
			+ "so huu san pham TEN_SP";

	public static String MT_END_DG = "Phien dau gia da ket thuc vao END_HOUR ngay"
			+ " END_DATE. Ban vui long tham gia dau gia tai phien tiep theo. "
			+ "Soan tin DK gui 9209 de duoc ho tro ve cu phap cac "
			+ "dich vu khac cua mPlus.Cam on ban da su dung dich vu "
			+ "cua MobiFone. DTHT 9244. Chi tiet tai http://m.mplus.vn";

	public static String MT_END_DG_HAVE_NEXT_SP = "Phien dau gia da ket thuc vao 19h50 ngay"
			+ " END_DATE. Ban vui long tham gia dau gia tai phien tiep theo luc 08h00 ngay"
			+ "  NEXT_START_DATE giai thuong la NEXT_TEN_SP."
			+ " Soan tin DK gui 9209 de duoc ho tro ve cu phap cac dich vu khac cua mPlus."
			+ "Cam on ban da su dung dich vu cua MobiFone.DTHT 9244."
			+ "Chi tiet tai http://m.mplus.vn";

	public static String MT_EXIST_MSG = "Ban da dang ky dich vu GAME DAU GIA"
			+ " truoc do. Ban co the tham gia dau gia ngay."
			+ " San pham dang dau gia la TEN_SP"
			+ " (gia thi truong la GIA_SP)."
			+ " Phien dau gia se ket thuc vao 14h ngay END_DATE."
			+ " Nhanh tay soan DG <muc gia> gui 9209 so huu TEN_SP"
			+ " neu ban la nguoi tra gia thap nhat va duy nhat."
			+ " VD: Ban tra gia SP la 25000d.Soan DG 25 gui 9209.";
	// + " Gia ban tra la boi so 1000";

	public static String MT_NOT_REGISTRY = "Ban chua dang ky dich vu."
			+ " Soan tin DK DAUGIA gui 9209 de co the tham gia ngay"
			+ " phien dau gia san pham TEN_SP tuan nay."
			+ " Lien he:  9244 hoac truy cap http://m.mplus.vn "
			+ " de biet them thong tin chi tiet";

	public static String MT_AMOUNT_EXIST = "Ban da dat gia la DG_AMOUNT."
			+ " Muc gia ban da dat truoc do. Nhanh tay dat gia khac de"
			+ " tang co hoi so huu san pham TEN_SP."
			+ " Hay soan DG <muc gia> gui 9209 so huu TEN_SP neu ban"
			+ " la nguoi tra gia thap nhat va duy nhat."
			+ " Chi tiet tai http://m.mplus.vn.DTHT:9244";

	public static String MT_MAX_SESSION = "Tin nhan khong hop le,"
			+ " theo quy dinh Spam cua Mobifone so dien thoai cua ban"
			+ " khong duoc nhan qua 30 tin/ngay. Ban vui long cho"
			+ " thong bao vao ngay mai de tham gia Dau gia tiep"
			+ " hoac su dung thue bao khac de tiep tuc dat gia."
			+ " Phien dau gia bat dau tu START_HOUR ngay START_DATE,"
			+ " ket thuc luc END_HOUR ngay END_DATE."
			+ " Truy cap: http://m.mplus.vn/ de xem the le."
			+ " Dien thoai ho tro: 9244.";

	public static String MT_NEW_SESSION = "So dien thoai cua Ban co the"
			+ " tiep tuc tham gia dau gia,"
			+ " soan tin: DG giadau gui 9209, luu y ban duoc"
			+ " phep nhan 30tin/ngay. Phien dau gia bat dau tu"
			+ " START_HOUR ngay START_DATE, ket thuc luc END_HOUR"
			+ " END_DATE. Truy cap: http://m.mplus.vn/ de xem the le."
			+ " Dien thoai ho tro: 9244.";

	public static String MT_WIN_TMP_LUCKY = "Thue bao USER_ID co muc"
			+ " gia Thap nhat va duy nhat da co thue bao"
			+ " khac dat trung. Chuc mung ban."
			+ " Muc gia DG_AMOUNT ma ban da dat dang la thap nhat va duy nhat."
			+ " Hay soan: DG [gia dau] gui 9209 de dat them cac muc "
			+ "gia khac de nang cao kha nang gianh chien thang";

	public static String MT_FIRST_DAUGIA = "Phien dau gia Nguoc dau tien"
			+ " cua MobiFone se dien ra vao NEXT_HOUR ngay NEXT_START_DATE."
			+ " Giai thuong chinh cua phien dau tien la"
			+ " NEXT_TEN_SP cung nhieu the cao co gia tri."
			+ " Ban vui long tro lai tham gia nhan tin dau gia"
			+ " khi chuong trinh bat dau.";

	public static String MT_WIN_SECOND = "Chuc mung ban chu nhan thue"
			+ " bao USER_ID ban la nguoi đat nhieu gia thap nhat va duy nhat"
			+ " tinh tu thoi diem bat dau den thoi diem ket thuc"
			+ " phien dau gia TEN_SP."
			+ " Giai thuong ban dat duoc la: Giai Nhi tri gia 500.000vnd,"
			+ " vui long lien he 1900571566 de duoc huong dan nhan giai."
			+ " Chuc ban may man o cac phien dau gia tiep theo."
			+ " Chi tiet tai http://m.mplus.vn.";

	public static String MT_WIN_THIRD = "Chuc mung ban chu nhan"
			+ " thue bao USER_ID ban la nguoi co so lan giao dich"
			+ " dau gia thanh cong nhieu nhat tinh tu thoi diem bat"
			+ " dau den thoi diem ket thuc phien dau gia TEN_SP."
			+ " Giai thuong ban dat duoc la: Giai Ba tri gia 300.000vnd,"
			+ " vui long lien he 1900571566 de duoc huong dan nhan giai."
			+ " Chuc ban may man o cac phien dau gia tiep theo."
			+ " Chi tiet tai http://m.mplus.vn.";

	public static String MT_ALERT_DAILY = "Muc gia dang chiem uu the nam trong khoang  AMOUNT_MIN den AMOUNT_MAX,"
			+ " va thuoc ve thue bao USER_ID. "
			+ "Chi can dat gia thap nhat va duy nhat la ban se so huu san pham."
			+ " Nhanh tay soan DG [gia dau] gui 9209.";

	public static String MT_WIN_DAILY = "Chuc mung ban chu nhan thue bao USER_ID"
			+ " ban la nguoi dat gia Thap nhat va Duy nhat la nguoi chien thang ngay"
			+ " CUR_DAY phien dau gia TEN_SP. "
			+ "Hay tiep tuc tham gia dau gia o ngay tiep theo de nang cao co hoi so huu san pham."
			+ " Chi tiet tai http://m.mplus.vn";

	public static String MT_NO_MIN_AND_ONLY_20 = "Chuc mung ban gia DG_AMOUNT ban vua dat la gia Duy nhat!"
			+ " Phan doan cua ban rat tot, day la 1 trong xx gia co co hoi can chien thang."
			+ " Hay dat them gia khac de nang cao kha nang chien thang, soan tin: DG [gia dau] gui 9209.";

	public static String MT_NO_MIN_AND_ONLY_FAR = "That tuyet! gia DG_AMOUNT ban vua dat la gia Duy nhat!"
			+ " Khoang AMOUNT_MIN - AMOUNT_MAX con nhieu muc gia chua ai dat va co kha nang chien thang cao hon."
			+ " Hay dat them gia khac de nang cao kha nang chien thang, soan tin: DG [gia dau] gui 9209.";

	public static String MT_NO_MIN_AND_ONLY_NEAR = "Chuc mung ban!"
			+ " Gia DG_AMOUNT ban vua dat la gia Duy nhat!"
			+ " Muc gia thap nhat va duy nhat hien tai dang thap hon muc gia ban dua ra."
			+ " Hay dat them gia khac de nang cao kha nang chien thang, soan tin: DG [gia dau] gui 9209.";
	// public static int TIME_SEND_MT_DAILY = 20;
	// public static int TIME_SEND_MT_WEEKLY = 9;
	public static String STRING_REGEX_REPLACE_AMOUNT = "DG_AMOUNT";
	public static String STRING_REGEX_REPLACE_USER_ID = "USER_ID";
	public static String STRING_REGEX_REPLACE_START_DATE = "START_DATE";
	public static String STRING_REGEX_REPLACE_END_DATE = "END_DATE";
	public static String STRING_REGEX_REPLACE_TEN_SP = "TEN_SP";
	public static String STRING_REGEX_REPLACE_GIA_SP = "GIA_SP";
	public static String STRING_REGEX_REPLACE_AMOUNT_MIN = "AMOUNT_MIN";
	public static String STRING_REGEX_REPLACE_AMOUNT_MAX = "AMOUNT_MAX";
	public static String STRING_REGEX_END_HOUR = "END_HOUR";
	public static String STRING_REGEX_START_HOUR = "START_HOUR";

	public static String STRING_REGEX_NEXT_HOUR = "NEXT_HOUR";
	public static String STRING_REGEX_NEXT_START_DATE = "NEXT_START_DATE";
	public static String STRING_REGEX_NEXT_TEN_SP = "NEXT_TEN_SP";
	public static String COMMAND_CODE = "DG";

}
