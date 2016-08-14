package pl.com.sebastianbodzak.infrastructure.devices;

import pl.com.sebastianbodzak.infrastructure.Scanner;

import java.io.*;

/**
 * Created by Dell on 2016-08-04.
 *
 * Simulation of product's barcode scanning. Scanner retrieves single bar-code from a file with many bar-codes.
 * Access to file based on path from instance variable 'path'. Second path 'tempPath' is needed for delete retrieved bar-code from file.
 * Eventually, after every scanning bar-code file is shorter by this bar-code.
 *
 */
public class SamsungScanner implements Scanner {

    private final String path;
    private final String tempPath;

    public SamsungScanner(String pathToBarCodeList, String pathForModifiedList) {
        this.path = pathToBarCodeList;
        this.tempPath = pathForModifiedList;
    }

    @Override
    public String getProductNumber() throws IOException {
        File barCodesFile = new File(path);
        BufferedReader fileReader = new BufferedReader(new FileReader(barCodesFile));

        String firstLine = scanFirstFileLine(fileReader);
        modifyAndCloseBarCodeFile(barCodesFile, firstLine, fileReader);

        return firstLine;
    }

    private String scanFirstFileLine(BufferedReader fileReader) throws IOException {
        String fileFirstLine = fileReader.readLine();
        if (!isEmpty(fileFirstLine))
            return fileFirstLine;
        return null;
    }

    private void modifyAndCloseBarCodeFile(File file, String lineToRemove, BufferedReader fileReader) throws IOException {
        File tempToolFile = new File(tempPath);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(tempToolFile));

        removeLineWithScannedBarCode(fileReader, fileWriter, lineToRemove);
        fileWriter.close();
        fileReader.close();
        prepareFile(file, tempToolFile);
    }

    private void removeLineWithScannedBarCode(BufferedReader fileReader, BufferedWriter fileWriter, String lineToRemove) throws IOException {
        String currentFileLine;
        while((currentFileLine = fileReader.readLine()) != null) {
            if(isFileLineToRemove(lineToRemove, currentFileLine)) continue;
            fileWriter.write(prepareLineForSave(currentFileLine));
        }
    }

    private boolean isFileLineToRemove(String lineToRemove, String currentFileLine) {
        return currentFileLine.trim().equals(lineToRemove);
    }

    private void prepareFile(File barCodesFile, File tempToolFile) {
        barCodesFile.delete();
        tempToolFile.renameTo(barCodesFile);
    }

    private String prepareLineForSave(String currentFileLine) {
        return currentFileLine + System.getProperty("line.separator");
    }

    private boolean isEmpty(String line) {
        return line == null || line.trim().length() == 0;
    }

}
