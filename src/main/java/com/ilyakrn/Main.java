package com.ilyakrn;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String pngPath = "C:\\Users\\IlyaKrn\\Desktop\\file.png";
        String binPath = "C:\\Users\\IlyaKrn\\Desktop\\bin.txt";
        String resultPath = pngPath + ".txt";
        byte[] pngBytes = Files.readAllBytes(Paths.get(pngPath));
        byte[] binBytes = Files.readAllBytes(Paths.get(binPath));



        String chunkName = "idAt";
        String endChunkName = "IEND";
        int binLength = binBytes.length;

        byte[] chunkNameBytes = chunkName.getBytes(StandardCharsets.US_ASCII);
        byte[] endChunkNameBytes = endChunkName.getBytes(StandardCharsets.US_ASCII);
        byte[] binLengthBytes = ByteBuffer.allocate(4).putInt(binLength).array();



        byte[] newChunk = new byte[12 + binLength];
        newChunk[0] = binLengthBytes[0];
        newChunk[1] = binLengthBytes[1];
        newChunk[2] = binLengthBytes[2];
        newChunk[3] = binLengthBytes[3];

        newChunk[4] = chunkNameBytes[0];
        newChunk[5] = chunkNameBytes[1];
        newChunk[6] = chunkNameBytes[2];
        newChunk[7] = chunkNameBytes[3];

        newChunk[newChunk.length - 4] = 0;
        newChunk[newChunk.length - 3] = 0;
        newChunk[newChunk.length - 2] = 0;
        newChunk[newChunk.length - 1] = 0;

        for (int i = 8; i < newChunk.length - 4; i++) {
            newChunk[i] = binBytes[i - 8];
        }

        byte[] pre = new byte[0];
        byte[] post = new byte[0];

        for (int i = 0; i < pngBytes.length - 3; i++) {
            if (pngBytes[i] == endChunkNameBytes[0] &&
                    pngBytes[i + 1] == endChunkNameBytes[1] &&
                    pngBytes[i + 2] == endChunkNameBytes[2] &&
                    pngBytes[i + 3] == endChunkNameBytes[3]
            ){
                pre = Arrays.copyOfRange(pngBytes, 0, i - 4);
                post = Arrays.copyOfRange(pngBytes, i - 4, pngBytes.length);
                break;
            }
        }

        File result = new File(resultPath);
        if (!result.exists())
            result.createNewFile();
        FileOutputStream fos = new FileOutputStream(result);
        byte[] res = new byte[pngBytes.length + newChunk.length];

        int currState = 0;
        for (int i = 0; i < pre.length; i++) {
            res[currState] = pre[i];
            currState++;
        }
        for (int i = 0; i < newChunk.length; i++) {
            res[currState] = newChunk[i];
            currState++;
        }
        for (int i = 0; i < post.length; i++) {
            res[currState] = post[i];
            currState++;
        }
        fos.write(res);
        fos.close();
    }
}