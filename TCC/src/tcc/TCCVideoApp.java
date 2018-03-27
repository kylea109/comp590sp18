package tcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TCCVideoApp {

	public static void main(String[] args) throws IOException, FileNotFoundException {
		
		// Must have proper dimensions
		int width = 800;
		int height = 450;
		int frames = 150;
		
		String filename = "C:/Users/kylea109/Desktop/Compression/candle.450p.yuv";
		File file = new File(filename);
		InputStream src = new FileInputStream(file);
		
		TCCEncoder encoder = new TCCEncoder(src, width, height, frames);
		
		File out_file = new File("C:/Users/kylea109/Desktop/Compression/candle_compressed.dat");
		OutputStream out_stream = new FileOutputStream(out_file);
		encoder.encode(out_stream);
		
		String filename2 = "C:/Users/kylea109/Desktop/Compression/candle_compressed.dat";
		File file2 = new File(filename2);
		InputStream src2 = new FileInputStream(file2);
	
		TCCDecoder decoder = new TCCDecoder(src2, width, height, frames);
		
		File out_file2 = new File("C:/Users/kylea109/Desktop/Compression/candle_decompressed.dat");
		OutputStream out_stream2 = new FileOutputStream(out_file2);
		decoder.decode(out_stream2);
		
	}

}
