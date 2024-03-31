package marumasa.marumasa_sign.util;

import marumasa.marumasa_sign.client.sign.TextureURLProvider;
import marumasa.marumasa_sign.type.TextureURL;

public class ImageRegister {
    static void registerDefault(String stringURL) {
        TextureURLProvider.loadedTextureURL(stringURL, TextureURL.minuma);
    }
}
