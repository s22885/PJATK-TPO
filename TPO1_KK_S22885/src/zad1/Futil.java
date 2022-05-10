package zad1;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class Futil {
    private static Charset chaFrom = Charset.forName("Cp1250");
    private static Charset chaTo = Charset.forName("UTF-8");

    public static void processDir(String dirIn, String outFile) {
        try {
            Files.delete(Paths.get(outFile));
        } catch (SecurityException | IOException ignore) {
        }

        try {
            Files.walkFileTree(Paths.get(dirIn), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path currFilePath, BasicFileAttributes attributes) throws IOException {
                    if (!attributes.isRegularFile()) {
                        return FileVisitResult.CONTINUE;
                    }
                    try (FileChannel inChannel = FileChannel.open(currFilePath, StandardOpenOption.READ);
                    ) {
                        ByteBuffer buffer = ByteBuffer.allocate(Math.toIntExact(inChannel.size()));
                        inChannel.read(buffer);

                        buffer.flip();
                        CharBuffer charBuffer = chaFrom.decode(buffer);
                        buffer = chaTo.encode(charBuffer);

                        FileChannel fcOut = FileChannel.open(Paths.get(
                                        outFile)
                                , StandardOpenOption.CREATE
                                , StandardOpenOption.WRITE
                                , StandardOpenOption.APPEND);

                        fcOut.write(buffer);
                        fcOut.close();

                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
