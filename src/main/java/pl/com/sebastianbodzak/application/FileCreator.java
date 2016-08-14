package pl.com.sebastianbodzak.application;

import pl.com.sebastianbodzak.domain.DataAccessException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by Dell on 2016-08-10.
 */
public class FileCreator {

    private final String path;
    private File file;

    public FileCreator(String path) {
        this.path = path;
        this.file = new File(path);
    }

    public void create(String ...singleString) throws DataAccessException, FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, true), true)){

                for (String s : singleString)
                    pw.print(s + "\n");

        } catch (Exception ex) {
            throw new DataAccessException("Can not create file", FileCreator.class);
        }
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }
}
