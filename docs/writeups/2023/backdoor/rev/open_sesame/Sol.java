public class Sol{
    private static final int[] valid_password = {52, 108, 49, 98, 97, 98, 97}; //4l1baba
    private static final String valid_user = "Jack Ma";
    public static void main(String[] args) {
        String str = "flag{" + flag(Integer.toString(sl4y3r(sh4dy("4l1baba"))), "U|]rURuoU^PoR_FDMo@X]uBUg") + "}";
        System.out.println(str);
    }

    private static boolean n4ut1lus(String str) {
        int[] it4chi = it4chi(str);
        if (it4chi.length != valid_password.length) {
            return false;
        }
        for (int i = 0; i < it4chi.length; i++) {
            if (it4chi[i] != valid_password[i]) {
                return false;
            }
        }
        return true;
    }

    private static int[] it4chi(String str) {
        int[] iArr = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            iArr[i] = str.charAt(i);
        }
        return iArr;
    }

    private static String sh4dy(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (Character.isDigit(charAt)) {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    private static int sl4y3r(String str) {
        return Integer.parseInt(str) - 1;
    }

    private static String flag(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str2.length(); i++) {
            sb.append((char) (str2.charAt(i) ^ str.charAt(i % str.length())));
        }
        return sb.toString();
    }

}
