package org.demo.chapter2.recipe7.utils;

/**
 * 此类模拟一个文本文件
 */
public class FileMock {

    /**
     * 存放文件内容
     */
    private String content[];
    /**
     * 当前处理的行数
     */
    private int index;

    /**
     * 构造方法 生成由随机字符组成的文件
     *
     * @param size:   文件的行数
     * @param length: 每行的长度
     */
    public FileMock(int size, int length) {
        content = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                int indice = (int) Math.random() * 255;
                buffer.append((char) indice);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    /**
     * 如果文件还有未处理的行则返回true，否则返回false
     *
     * @return 文件未处理完毕则返回true，否则返回false
     */
    public boolean hasMoreLines() {
        return index < content.length;
    }

    /**
     * 返回文件的下一行，如果没有更多行，则返回null
     *
     * @return 文件的下一行，如果没有更多行，则返回null
     */
    public String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length - index));
            return content[index++];
        }
        return null;
    }

}
