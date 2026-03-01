package com.omeralkan.customer.util;
import java.util.regex.Pattern;
public class TCKNDogrulama {
    private static final Pattern SADECE_RAKAM = Pattern.compile("^\\d+$");
    private TCKNDogrulama() {
        throw new UnsupportedOperationException("Bu bir Utility sınıfıdır, nesne üretilemez!");
    }
    public static boolean tcknGecerliMi(String tckn) {

        if (tckn == null || tckn.length() != 11 || !SADECE_RAKAM.matcher(tckn).matches()) {
            return false;
        }
        if (tckn.charAt(0) == '0') {
            return false;
        }

        boolean tumAyni = true;
        for (int i = 1; i < 11; i++) {
            if (tckn.charAt(i) != tckn.charAt(0)) {
                tumAyni = false;
                break;
            }
        }
        if (tumAyni) return false;

        int tekToplam = 0;
        int ciftToplam = 0;
        int ilkOnToplam = 0;

        for (int i = 0; i < 9; i++) {

            int rakam = tckn.charAt(i) - '0';

            if (i % 2 == 0) {
                tekToplam += rakam;
            } else {
                ciftToplam += rakam;
            }
            ilkOnToplam += rakam;
        }

        int onuncuHane = ((tekToplam * 7) - ciftToplam) % 10;
        if (onuncuHane < 0) {
            onuncuHane += 10;
        }

        int gercekOnuncuHane = tckn.charAt(9) - '0';
        if (gercekOnuncuHane != onuncuHane) return false;

        ilkOnToplam += gercekOnuncuHane;
        int onbirinciHane = ilkOnToplam % 10;
        int gercekOnbirinciHane = tckn.charAt(10) - '0';

        return gercekOnbirinciHane == onbirinciHane;
    }
}