package su.msu.cs.lvk.accorute.http.utils;

import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {
    /**
     * Get the parent URI of the supplied URI
     * @param url The input URI for which the parent URI is being requrested.
     * @return The parent URI.  Returns a URI instance equivalent to "../" if
     * the supplied URI path has no parent.
     */
    public static URL getParent(URL url) {
        String parentPath;
        if (url.getPath() == null || "".equals(url.getPath().trim()) || "/".equals(url.getPath().trim())) {
            parentPath = "/";
        } else {
            parentPath = url.getPath().replace('\\', '/');
            parentPath = StringUtils.chomp(parentPath, "/");
            parentPath = parentPath.substring(0, parentPath.lastIndexOf("/") + 1);
        }

        try {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), parentPath);
        } catch (MalformedURLException muex) {
            throw new IllegalStateException("Valid URL split into parts and reassembled again cannot throw MalformedURLException");
        }
    }

    public static boolean isDirectory(URL url) {
        if (url.getPath() == null || "".equals(url.getPath().trim())) return true;

        if (url.getQuery() != null) return false;

        if (url.getPath().endsWith("/")) return true;

        int lastSlashPos = url.getPath().lastIndexOf("/");
        int lastDotPos = url.getPath().lastIndexOf(".");

        //обрабатываем, только если lastSlashPos < lastDotPos
        //варианты: "file" (-1 >= -1 - true)
        // "/file.file/" (11 >= 6 - true)
        // "/file" (1 >= -1 - true)
        // "file.txt" - (-1 >= 5 - false)
        return lastSlashPos >= lastDotPos;
    }

    /**
      * Обрезает расширение файла, если оно имеется.
      * @param path - имя файла.
      * @return возвращает имя файла без расширения. Если у файла уже не расширения, то ничего не делает.
      */
     public static String cutOfPathExt(String path) {
         int lastSlashPos = path.lastIndexOf("/");
         int lastDotPos = path.lastIndexOf(".");

         //обрабатываем, только если lastSlashPos < lastDotPos
         //варианты: "file" (-1 < -1 - false)
         // "/file.file/" (11 < 6 - false)
         // "/file" (1 < -1 - false)
         // "file.txt" - (-1 < 5 - true)
         if (lastSlashPos < lastDotPos) return path.substring(0, lastDotPos);

         return path;
     }

    /**
      * Возвращает короткое имя файла из полного пути.
      * @param path - полное имя файла.
      * @return возвращает короткое имя файла. Если путь - директория - возвращает пустую строку.
      */
     public static String getShortFileName(String path) {
         int lastSlashPos = path.lastIndexOf("/");
         int lastDotPos = path.lastIndexOf(".");

         //обрабатываем, только если lastSlashPos < lastDotPos
         //варианты: "file" (-1 < -1 - false)
         // "/file.file/" (11 < 6 - false)
         // "/file" (1 < -1 - false)
         // "file.txt" - (-1 < 5 - true)
         if (lastSlashPos < lastDotPos) return path.substring(lastSlashPos);

         return "";
     }

     /**
      * Меняет существующее расширение файла на новое.
      * @param path - имя файла.
      * @param newExt  - новое расширение файла (с точкой!).
      * @return возвращает имя файла с новым расширением. Если у файла не было расширения, то оно появится.
      */
     public static String changePathExt(String path, String newExt) {
         return cutOfPathExt(path) + newExt;
     }

    /**
     *
     * @param path
     * @return возвращает расширение файла пути вместе с точкой
     */
    public static String getPathExt(String path) {
        int lastSlashPos = path.lastIndexOf("/");
        int lastDotPos = path.lastIndexOf(".");

        //обрабатываем, только если lastSlashPos < lastDotPos
        //варианты: "file" (-1 < -1 - false)
        // "/file.file/" (11 < 6 - false)
        // "/file" (1 < -1 - false)
        // "file.txt" - (-1 < 5 - true)
        if (lastSlashPos < lastDotPos) return path.substring(lastDotPos);

        return "";

    }
}


