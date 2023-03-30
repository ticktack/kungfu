package org.kungfu.util;

import java.io.*;

public class FileKit {

    public static final void writeFileContent(String path, String content) {

        try {
            //写入中文字符时解决中文乱码问题
            FileOutputStream fos = new FileOutputStream(new File(path), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            //简写如下：
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path)), "UTF-8"));

            bw.append(content);

            // 注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
            bw.flush();
            bw.close();
            osw.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
