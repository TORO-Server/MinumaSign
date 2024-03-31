package marumasa.marumasa_sign.client.sign;

import marumasa.marumasa_sign.type.TextureURL;
import marumasa.marumasa_sign.util.ImageRequest;
import marumasa.marumasa_sign.util.Utils;

import java.util.*;

public class TextureURLProvider {

    private static final Map<String, TextureURL> loaded = new HashMap<>();// 読み込み済みテクスチャマップ
    private static final Map<String, List<String>> loading = new HashMap<>();// 読み込み中テクスチャマップ
    private static final List<String> failure = new ArrayList<>();// 読み込み失敗テクスチャリスト


    public static TextureURL get(String stringURL, String signText) {

        if (loaded.containsKey(stringURL)) {

            return loaded.get(stringURL);

        } else if (loading.containsKey(stringURL)) {

            List<String> customSignList = loading.get(stringURL);
            customSignList.add(signText);
            loading.put(stringURL, customSignList);
            return TextureURL.loading;

        } else if (failure.contains(stringURL)) {

            return TextureURL.error;

        }

        loading.put(stringURL, new ArrayList<>(Collections.singleton(signText)));

        ImageRequest.add(stringURL);

        // 読み込み中の画像を表示する
        return TextureURL.loading;
    }

    public static void loadedTextureURL(String stringURL, TextureURL textureURL) {

        List<String> signTextList = loading.remove(stringURL);

        loaded.put(stringURL, textureURL);
        for (String signText : signTextList) {
            CustomSignProvider.changeSignTexture(textureURL, signText);
        }
    }

    protected static void removeCache() {

        // 登録されているテクスチャ削除
        for (TextureURL textureURL : loaded.values())
            Utils.destroyTexture(textureURL.identifier());

        loaded.clear();
        loading.clear();
        failure.clear();
    }
}
