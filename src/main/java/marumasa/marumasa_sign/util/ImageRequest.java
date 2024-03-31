package marumasa.marumasa_sign.util;

import java.util.ArrayDeque;
import java.util.Queue;

public class ImageRequest {
    private static final Queue<String> queue = new ArrayDeque<>();

    public static int queueSize() {
        return queue.size();
    }


    public static void add(
            // 画像のURL
            String stringURL
    ) {
        queue.add(stringURL);
    }

    record ImageLoader(String stringURL) implements Runnable {
        public void run() {
            getURL(stringURL);
        }
    }

    public static void load(int maxThreads) {

        // 読み込みスレッド作成
        final Thread[] threadList = new Thread[maxThreads];
        for (int i = 0; i < threadList.length; i++) {
            if (queue.size() == 0) break;
            String stringURL = queue.remove();
            threadList[i] = new Thread(new ImageLoader(stringURL));
            threadList[i].setName(String.format("ImageLoader-%d", i));
        }
        try {

            // 読み込み開始
            for (Thread thread : threadList)
                if (thread != null) thread.start();
                else break;

            // 読み込み終わるまで待機
            for (Thread thread : threadList)
                if (thread != null) thread.join();
                else break;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getURL(String stringURL) {
        ImageRegister.registerDefault(stringURL);
    }
}
