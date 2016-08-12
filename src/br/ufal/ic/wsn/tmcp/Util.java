package br.ufal.ic.wsn.tmcp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {
	public static String readFile(String path) {
		try {
			return new String(
					Files.readAllBytes(Paths.get(path)),
					StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
