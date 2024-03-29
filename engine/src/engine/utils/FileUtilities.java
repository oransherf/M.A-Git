package engine.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtilities
{
    public static void modifyTxtFile(Path i_Path, String i_Content) throws IOException
    {
        // this method gets a path to a file and content and overwrites the file content to i_Content
        FileOutputStream writer = null;
        writer = new FileOutputStream(String.valueOf(i_Path.toString()));
        writer.write(("").getBytes());
        writer.write(i_Content.getBytes());
        writer.close();
    }

    public static void createAndWriteTxtFile(Path i_Path, String i_Content) throws IOException
    {
        try (Writer out1 = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(i_Path.toString()), "UTF-8")))
        {
            out1.write(i_Content);
        }
    }

    public static void createZipFileFromContent(String i_ZipName, String i_Content , String i_NameOfTxtFileInsideZip, Path i_MagitDir) throws IOException
    {
        // this method creates a zip - his name is i_ZipName , he contains a txt file named i_NameOfTxtFileInsideZip
        // and the content of the txt file is i_Content

        if(FilenameUtils.getExtension(i_NameOfTxtFileInsideZip).equals(""))
        {
            i_NameOfTxtFileInsideZip = i_NameOfTxtFileInsideZip.concat(".txt");
        }
        Path createdTempTxtPath = i_MagitDir.resolve("objects").resolve(i_NameOfTxtFileInsideZip);
        createAndWriteTxtFile(createdTempTxtPath, i_Content);
        zip(i_ZipName, createdTempTxtPath, i_MagitDir);
        deleteFile(createdTempTxtPath);
    }

    public static void zip(String i_ZipName, Path i_FilePath, Path i_MagitDir) throws IOException
    {
        // this method recieves a path to a file and name , and creates a zip file name i_ZipName in objects dir

        Path zippingPath = i_MagitDir.resolve("objects").resolve(i_ZipName + ".zip");
        String sourceFile = i_FilePath.toString();
        FileOutputStream fos = new FileOutputStream(zippingPath.toString());
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0)
        {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public static void unzip(String i_ZipName, Path i_DestToUnZip, Path i_MagitDir) throws IOException
    {
        // this method recieves a zip name and destination path and unzipping the file name i_Zipname to destination

        String fileZip = i_MagitDir.resolve("objects").resolve(i_ZipName).toString();
        File destDir = new File(i_DestToUnZip.toString());
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null)
        {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0)
            {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static int getNumberOfSubNodes(Path i_Path) throws IOException
    {
        // this method return number of sub non empty nodes

        List<Path> ListOfSubFolders = Files.walk(i_Path, 1)
                .filter(d -> d.toFile().isDirectory())
                .filter(d->d!= i_Path)
                .collect(Collectors.toList());
        int numOfSubBlobs = (int) Files.walk(i_Path, 1)
                .filter(d -> !d.toFile().isDirectory())
                .count();
        int numOfSubFolders = 0;
        for (Path path : ListOfSubFolders)
        {
            if (Files.walk(path, 1).count() - 1 != 0)
            {
                numOfSubFolders++;
            }
        }
        return numOfSubFolders + numOfSubBlobs;
    }

    public static void deleteFile(Path i_FileToDelete) throws IOException
    {
        Files.delete(i_FileToDelete);
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException
    {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator))
        {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static String getTxtFromZip(String i_ZipFilePath, String i_TxtFileNameInZip) throws IOException
    {
        //read File From Zip Without extract it

        ZipFile zipFile = new ZipFile(i_ZipFilePath);
        ZipEntry zipEntry = zipFile.getEntry(i_TxtFileNameInZip);

        InputStream inputStream = zipFile.getInputStream(zipEntry);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1)
        {
            result.write(buffer, 0, length);
        }
        zipFile.close();
        inputStream.close();
        result.close();

        return result.toString("UTF-8");
    }

    public static boolean isDirectoryInFileSystem(Path i_dirToCheck)
    {
        return Files.isDirectory(i_dirToCheck);
    }

    public static boolean isExists(Path i_Path)
    {
        try
        {
            return Files.exists(i_Path);
        }
        catch (SecurityException ex)
        {
            throw new SecurityException("read access to " + i_Path + " is denied");
        }
    }

    public static boolean isFolder(Path i_Path) { return FilenameUtils.getExtension(i_Path.toString()).equals("");}

    public static void copyDirectory(Path i_Source, Path i_Destination) throws IOException
    {
        File sourceFile = new File(i_Source.toString());
        File destinationFile = new File(i_Destination.toString());
        FileUtils.copyDirectory(sourceFile, destinationFile);
    }

    public static String getFileContent(Path i_RRNameFilePath) throws IOException
    {
        return new String(Files.readAllBytes(i_RRNameFilePath));
    }

    public static void cleanDirectory(Path i_Destination) throws IOException
    {
        FileUtils.cleanDirectory(new File(i_Destination.toString()));
    }

    public static void moveFile(Path i_Source, Path i_Dest) throws IOException
    {
        Files.move(i_Source, i_Dest, REPLACE_EXISTING);
    }

    public static void copyDirectoryContent(Path i_Source, Path i_Dest)
    {
        try (Stream<Path> walk = Files.walk(i_Source))
        {
            List<Path> result = walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for(Path path : result)
            {
                if(!path.getFileName().toString().equals("HEAD.txt"))
                {
                    copyFile(path, i_Dest.resolve(path.getFileName()));
                }
            }
        }
        catch (IOException e)
        {
            //TODO
            e.printStackTrace();
        }
    }

    public static void copyFile(Path i_Source, Path i_Dest) throws IOException
    {
        Files.copy(i_Source, i_Dest, REPLACE_EXISTING);
    }
}
