package com.bt.camnav.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DataExtractorUtil {

    public static final String[] DEFAULT_KEYS = { "date", "website", "visits" };
    public static final String EMPTY_JSON = "{}";

    private static void closeOPCPackage(OPCPackage pkg) {
        if (pkg != null) {
            try {
                pkg.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeData(File dir, String filename, JsonArray array) throws IOException {
        // write json array into output file
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, filename)), "utf-8"));
            writer.write(array.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static void extract(String filename, File outputDir) {
        extract(filename, true, outputDir);
    }

    public static void extract(String filename, boolean firstRowIsHeader, File outputDir) {
        File file = new File(filename);
        extract(file, firstRowIsHeader, outputDir);
    }

    public static String extract(File file, boolean firstRowIsHeader, File outputDir) {
        String filename = file.getName();
        String simple = filename.substring(0, filename.lastIndexOf('.'));
        String out = (new StringBuilder(simple)).append(".json").toString();

        OPCPackage pkg = null;
        try {

            pkg = OPCPackage.open(file);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            String[] keys = DEFAULT_KEYS;
            int length = keys.length;
            boolean begin = !firstRowIsHeader;

            JsonArray array = new JsonArray();

            Sheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (begin) {
                    JsonObject json = new JsonObject();
                    int ind = 0;

                    int lastColumnIndex = -1;

                    for (Cell cell : row) {
                        int currentColumnIndex = cell.getColumnIndex();
                        if (lastColumnIndex < 0) {
                            lastColumnIndex = currentColumnIndex - 1;
                        }

                        int indexDiff = currentColumnIndex - lastColumnIndex;

                        if (indexDiff > 1) {
                            ind += indexDiff - 1;
                        }

                        if (ind < length) {
                            String value = null;
                            switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                value = String.format("%1$,.0f", cell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            }
                            if (value != null && value.trim().length() > 0) {
                                json.addProperty(keys[ind], value);
                            }
                        }
                        ind++;
                        lastColumnIndex = currentColumnIndex;
                    }

                    // validate json object, do not add empty json object!
                    if (!EMPTY_JSON.equals(json.toString())) {
                        array.add(json);
                    }
                }
                if (firstRowIsHeader) {
                    begin = true;
                }
            }

            writeData(outputDir, out, array);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOPCPackage(pkg);
        }
        return out;
    }

    public static void extractFiles(String dir, File outputDir) {
        File file = new File(dir);
        if (file != null) {
            boolean firstRowIsHeader = true;

            JsonArray array = new JsonArray();

            if (file.isDirectory()) {
                // create new filename filter
                FilenameFilter fileNameFilter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        if (name.lastIndexOf('.') > 0) {
                            int lastIndex = name.lastIndexOf('.');
                            String ext = name.substring(lastIndex);
                            return ".xlsx".equalsIgnoreCase(ext);
                        }
                        return false;
                    }
                };

                for (File f : file.listFiles(fileNameFilter)) {
                    String name = extract(f, firstRowIsHeader, outputDir);
                    JsonObject json = new JsonObject();
                    json.addProperty("name", name);
                    array.add(json);
                }
            } else {
                String name = extract(file, firstRowIsHeader, outputDir);
                JsonObject json = new JsonObject();
                json.addProperty("name", name);
                array.add(json);
            }

            String filename = "files.json";
            try {
                writeData(outputDir, filename, array);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}