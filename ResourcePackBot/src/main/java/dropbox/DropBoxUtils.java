package dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.util.IOUtil;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class DropBoxUtils {
    private static final String ACCESS_TOKEN = "NL60Sluig84AAAAAAAAAARtBfBqO7DRGCZuAB0JoUxRX-UuWyYAkbctqDV_h-l54";
    private static final String CLIENT_IDENTIFIER = "TexturePackBot";

    private static DbxClientV2 client;

    public static void build() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/" + CLIENT_IDENTIFIER).build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public static void printAccountName() {
        try {
            FullAccount account = client.users().getCurrentAccount();
            System.out.println(account.getName().getDisplayName());
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads a file in a single request. This approach is preferred for small files since it
     * eliminates unnecessary round-trips to the servers.
     *
     * @param localFile local file to upload
     * @param dropboxPath Where to upload the file to within Dropbox
     */
    public static void uploadFile(File localFile, String dropboxPath) {
        try (InputStream in = new FileInputStream(localFile)) {
            IOUtil.ProgressListener progressListener = l -> printProgress(l, localFile.length());

            FileMetadata metadata = client.files().uploadBuilder(dropboxPath + localFile.getName())
                    .withMode(WriteMode.ADD)
                    .withClientModified(new Date(localFile.lastModified()))
                    .uploadAndFinish(in, progressListener);

            System.out.println(metadata.toStringMultiline());
        } catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading from file \"" + localFile + "\": " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void printProgress(long uploaded, long size) {
        System.out.printf("Uploaded %12d / %12d bytes (%5.2f%%)\n", uploaded, size, 100 * (uploaded / (double) size));
    }
}
