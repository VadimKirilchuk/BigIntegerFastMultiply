/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

import java.math.BigInteger;
import java.util.Random;
import java.io.*;
/**
 *
 * @author chibis
 */
public class TestBigNumberPerf {



    private TestBigNumberPerf() {
    }

    public static void runAll() throws Exception {
	int minimumIterations = 20;
	int maxIterations = 100;
	int dispersionTrust = 80;
	int from = 2048;
	int points = 8;

	//add("ADD.txt", from * 2048, points, minimumIterations, dispersionTrust, maxIterations);
	//sub("SUB.txt", from * 2048, points, minimumIterations, dispersionTrust, maxIterations);
	//mul("MUL.txt", from, points, minimumIterations, dispersionTrust, maxIterations);
	//mulFFT("FFT.txt", from, points / 2, minimumIterations / 2, dispersionTrust, maxIterations / 2);
	//mulFFT2("FFT2.txt", from, points / 2, minimumIterations / 2, dispersionTrust, maxIterations / 2);
	//mulFFT1vsFFT2("FFT1vsFFT2.txt", from, points / 2, minimumIterations / 2, dispersionTrust, maxIterations / 2);
	mulFFT2vsFFT3("FFT2vsFFT3.txt", from, points / 2, minimumIterations / 2, dispersionTrust, maxIterations / 2);
    }

    private static Random rnd = new Random();
    
    private static BigNumber bn1;
    private static BigNumber bn2;

    private static BigInteger b1;
    private static BigInteger b2;
    
    private static Dispersion disp;
    private static int delta;
    private static long t1;
    private static long t2;
    
    public static void add(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("BigNum.add ");
	out.print("BigInt.add ");
	out.println();

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    out.print(i);
	    out.print(" ");	    
	    
	    init( i,minDispersion, dispersionTrust, maxIterations);
	
	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.add(bn2);
		t2 = System.currentTimeMillis();
		//System.out.println(Math.sqrt(disp.getDispersion())/disp.getMean());
		delta = (int) (t2 - t1);
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		b1.add(b2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }

    public static void mul(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("BigNum.mul ");
	out.print("BigInt.mul ");
	out.println();

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    out.print(i);
	    out.print(" ");

	    init(i, minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.mul(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
		System.out.println(Math.sqrt(disp.getDispersion()) / disp.getMean());
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		b1.multiply(b2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }

    public static void mulFFT(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("BigNum.FFT ");
	out.print("BigInt.mul ");
	out.println();

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    out.print(i);
	    out.print(" ");

	    init(i, minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.mulFFT(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    //System.out.println(Math.sqrt(disp.getDispersion()) / disp.getMean());
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		b1.multiply(b2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }

    public static void mulFFT2(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("BigNum.FFT2 ");
	out.print("BigInt.mul ");
	out.println();

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    out.print(i);
	    out.print(" ");

	    init(i, minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.mulFFT2(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    //System.out.println(Math.sqrt(disp.getDispersion()) / disp.getMean());
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		b1.multiply(b2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }
    
    public static void mulFFT1vsFFT2(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("recursive ");
	out.print("iterative ");
	out.println();

	byte[] byteArray1;
	byte[] byteArray2;

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    byteArray1 = new byte[i];
	    byteArray2 = new byte[i];

	    out.print(i);
	    out.print(" ");

	    // Инициализация массива	
	    for (int j = 0; j < i; ++j) {
		byteArray1[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
		byteArray2[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	    }

	    BigNumber bn1 = new BigNumber(byteArray1);
	    BigNumber bn2 = new BigNumber(byteArray2);

	    BigNumber bn3 = new BigNumber(byteArray1);
	    BigNumber bn4 = new BigNumber(byteArray2);

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);
	    delta = 1;

	    t1=0;
	    t2=0;

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.mulFFT(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    //System.out.println(Math.sqrt(disp.getDispersion()) / disp.getMean());
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn3.mulFFT2(bn4);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }    
    
    public static void mulFFT2vsFFT3(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("iterative ");
	out.print("shortIter ");
	out.println();

	byte[] byteArray1;
	byte[] byteArray2;

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    byteArray1 = new byte[i];
	    byteArray2 = new byte[i];

	    out.print(i);
	    out.print(" ");

	    // Инициализация массива	
	    for (int j = 0; j < i; ++j) {
		byteArray1[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
		byteArray2[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	    }

	    BigNumber bn1 = new BigNumber(byteArray1);
	    BigNumber bn2 = new BigNumber(byteArray2);

	    BigNumber bn3 = new BigNumber(byteArray1);
	    BigNumber bn4 = new BigNumber(byteArray2);

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);
	    delta = 1;

	    t1=0;
	    t2=0;

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.mulFFT2(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    //System.out.println(Math.sqrt(disp.getDispersion()) / disp.getMean());
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn3.mulFFT3(bn4);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }    
    
    public static void sub(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);;

	out.print("Bytes ");
	out.print("BigNum.add ");
	out.print("BigInt.add ");
	out.println();

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    out.print(i);
	    out.print(" ");

	    init(i, minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.sub(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		b1.subtract(b2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }

    public static void div(String fileName, int fromDim, int mulToEnd,
	    int minDispersion, int dispersionTrust, int maxIterations) throws Exception {

	PrintWriter out = createFile(fileName);

	out.print("Bytes ");
	out.print("BigNum.div ");
	out.print("BigInt.div ");
	out.println();

	for (int i = fromDim; i < fromDim * Math.pow(2, mulToEnd); i *= 2) {

	    out.print(i);
	    out.print(" ");

	    init(i, minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		bn1.div(bn2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }

	    out.print(disp.getMean());
	    out.print(" ");

	    disp = new Dispersion(minDispersion, dispersionTrust, maxIterations);

	    while (!disp.canTrust(delta)) {
		t1 = System.currentTimeMillis();
		b1.divide(b2);
		t2 = System.currentTimeMillis();
		delta = (int) (t2 - t1);
	    }
	    out.print(disp.getMean());
	    out.println();
	}
	out.close();
    }
    
    public static PrintWriter createFile(String fileName) throws IOException{
	File file = new File(fileName);
	if (!file.exists()) {
	    file.createNewFile();
	}
	return new PrintWriter(file);
    }
    
    public static void init(int i, int minDispersion, int dispersionTrust, int maxIterations){

	byte[] byteArray1 = new byte[i];
	byte[] byteArray2 = new byte[i];

	// Инициализация массива	
	for (int j = 0; j < i; ++j) {
	    byteArray1[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	    byteArray2[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	}
	
	bn1 = new BigNumber(byteArray1);
	bn2 = new BigNumber(byteArray2);
	b1 = new BigInteger(byteArray1);
	b2 = new BigInteger(byteArray2);

	disp=new Dispersion(minDispersion, dispersionTrust, maxIterations);
	delta=1;
	t1=0;
	t2=0;
	
    }
}
