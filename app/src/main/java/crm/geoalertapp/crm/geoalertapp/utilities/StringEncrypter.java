package crm.geoalertapp.crm.geoalertapp.utilities;

import se.simbio.encryption.Encryption;

public class StringEncrypter {

    private static final String KEY = "$3creTQei";
    private static final String SALT = "anotherS@lt";
    private static final byte[] IV = {-21, 58, 41, 124, -17, -19, 47, -35, 115, 120, -41, -7, 127, 103, -91, 8};

    public static String encrypt(String string) {
        Encryption encryption = Encryption.getDefault(KEY,SALT, IV);
        return encryption.encryptOrNull(string);
    }

    public static String decrypt(String string) {
        // doesnt work on server (requires gradle build)
        Encryption encryption = Encryption.getDefault(KEY, SALT, IV);
        return encryption.decryptOrNull(string);
    }
}
