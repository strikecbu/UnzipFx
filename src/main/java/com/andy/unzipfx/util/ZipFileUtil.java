package com.andy.unzipfx.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ZipFileUtil {

    private static File getTempFolder() {
        final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
        return new File(TEMP_FOLDER, "temp_${System.currentTimeMillis()}");
    }

    public static List<File> fileToSplit(File file, int length) throws IOException {
        File tempFolder = getTempFolder();

        System.out.println(tempFolder.getAbsolutePath());
        File splitZipFolder = new File(tempFolder, "split");
        splitZipFolder.mkdirs();
        List<File> result = new ArrayList<File>();
        byte[] arrays = Files.readAllBytes(file.toPath());
        ByteBuffer wrap = ByteBuffer.wrap(arrays);
        int index = 0;
        int fileIndex = 1;
        while (index < arrays.length) {
            int size = length;
            if (index + size > arrays.length) {
                size = arrays.length - index;
            }

            byte[] bytes = new byte[size];
            wrap.get(bytes, 0, bytes.length);
            Path path = new File(splitZipFolder, String.valueOf(fileIndex)).toPath();
            Files.write(path, bytes, StandardOpenOption.CREATE);
            result.add(path.toFile());
            index += length;
            fileIndex++;
        }
        return result;
    }

    /**
     * 復原規則重點:
     * 切割檔名是數字不帶任何副檔名，會做檔案排序後再合併
     *
     * @param files
     * @param fileType 附檔名 ex: zip
     * @return
     */
    public static File splitToFile(List<File> files, String fileType) throws IOException {
        File tempFolder = getTempFolder();
        tempFolder.mkdirs();

        Optional<byte[]> combineBytes = files.stream()
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getName())))
                .map(file -> {
                    try {
                        return Files.readAllBytes(file.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                })
                .reduce((last, current) -> {
                    return ByteBuffer.allocate(last.length + current.length)
                            .put(last)
                            .put(current)
                            .array();
                });
        File file = new File(tempFolder, "combine." + fileType);
        byte[] bytes = combineBytes.get();
        Files.write(file.toPath(), bytes, StandardOpenOption.CREATE);
        System.out.println(file.getAbsolutePath());
        return file;
    }

    public static List<File> zipAllFiles(List<File> files, String password) {
        File folder = getTempFolder();
        folder.mkdirs();
        System.out.println(folder.getAbsolutePath());
        return files.stream()
                .map(file -> {
                    ZipParameters zipParameters = new ZipParameters();
                    zipParameters.setEncryptFiles(true);
                    zipParameters.setEncryptionMethod(EncryptionMethod.AES);
                    zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                    File zip = new File(folder, file.getName() + ".zip");
                    ZipFile zipFile = new ZipFile(zip, password.toCharArray());
                    try {
                        zipFile.addFile(file, zipParameters);
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
                    return zip;
                }).collect(Collectors.toList());

    }

    public static List<File> unzipAllFiles(List<File> files, String password) {
        File folder = getTempFolder();
        folder.mkdirs();
        System.out.println(folder.getAbsolutePath());
        return files.stream()
                .map(file -> {
                    try {
                        new ZipFile(file, password.toCharArray()).extractAll(folder.getAbsolutePath());
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
                    return new File(folder, file.getName().substring(0, file.getName().lastIndexOf(".")));
                })
                .collect(Collectors.toList());
    }


    public static List<File> changeFileType(File folder, String fileType) {
        return changeFileType(Arrays.asList(Objects.requireNonNull(folder.listFiles())), fileType);
    }

    public static List<File> changeFileType(List<File> files, String fileType) {
        return files.stream()
                .map(file -> {
                    String fileName = file.getName();
                    if (fileType == null) {
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    } else if (file.getName().lastIndexOf(".") == -1) {
                        fileName = fileName + "." + fileType;
                    } else {
                        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "." + fileType;
                    }
                    try {
                        return Files.move(file.toPath(), new File(file.getParentFile(), fileName).toPath()).toFile();
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                }).collect(Collectors.toList());
    }


    public static void main(String[] args) throws IOException {
        String path ="C:/Users/1510002/Downloads/AAA/sharedLib.zip";
        List<File> files = ZipFileUtil.fileToSplit(new File(path), 6291456);
        files = changeFileType(files, "vip");
        files = zipAllFiles(files, "Cola2022");
        files = unzipAllFiles(files, "Cola2022");
        files = changeFileType(files, null);
        splitToFile(files, "zip");

//        def splitZipFiles = ZipFileUtil2.fileToSplit(checkinResources.lib, Integer.parseInt(env.getProperty('mail.shared-lib.zip.size')))
//        splitZipFiles = ZipFileUtil2.changeFileType(splitZipFiles, 'vip')
//        splitZipFiles = ZipFileUtil2.zipAllFiles(splitZipFiles, password)
    }
}
