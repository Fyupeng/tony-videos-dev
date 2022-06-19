package com.tony.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    private String ffmpegEXE;

    private static final String TEMP_FILE_NAME = "tempVideo.mp4";

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }




    private void removeAudio(String videoInputPath, String videoOutputPath) throws Exception {
        // ffmpeg.exe -i D:\45.mp4 -an D:\45_template.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-c:v");
        command.add("copy");

        command.add("-an");
        command.add(videoOutputPath);

        command.add("-y");

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        MergeVideoMp3.CloseStream(process);

    }

    public void convertor(String videoInputPath, String mp3InputPath,
                          double seconds, String videoOutputPath) throws Exception{
        //System.out.println(videoInputPath);
        //System.out.println(mp3InputPath);
        //System.out.println(seconds);
        //System.out.println(videoOutputPath);
        //复制原视频一份去除音轨
        String tempVideoOutputPath =
                videoOutputPath.substring(0, videoOutputPath.lastIndexOf("/")) + "/" + TEMP_FILE_NAME;

        removeAudio(videoInputPath, tempVideoOutputPath);

        String tempVideoInputPath = tempVideoOutputPath;

        //ffmpeg.exe -i D:\45.mp4 -i "E:\CloudMusic\二次元\深町純 - 绵雪.mp3" -t 10 -y 新的视频.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(tempVideoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-c:v");
        command.add("copy");

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);

        //for(String c : command){
        //    System.out.println(c);
        //}

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        MergeVideoMp3.CloseStream(process);

    }

    private static void CloseStream(Process process) throws IOException {
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while( (line = br.readLine())  != null ){

        }

        if(br != null){
            br.close();
        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }
        if(errorStream != null){
            errorStream.close();
        }
    }

    public static void main(String[] args) {
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("D:/study/software/ffmpeg/bin/ffmpeg.exe");
        try {
            ffmpeg.convertor("D:/45.mp4", "D:/1.mp3", 10.1, "D:/通过java生成的视频.avi");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
