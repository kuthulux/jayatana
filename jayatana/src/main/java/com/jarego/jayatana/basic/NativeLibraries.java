package com.jarego.jayatana.basic;

import java.io.File;

import com.jarego.jayatana.Feature;

public class NativeLibraries implements Feature {
	@Override
	public void deploy() {
		// cargar librerias para soporte swing
		System.loadLibrary("jawt");
		// cargar libreria de JAyatana
		if (System.getenv("JAYATANA_NATIVEPATH") != null) {//opcion para desarrollo
			System.load(System.getenv("JAYATANA_NATIVEPATH"));
			System.err.println("JAYATANA_NATIVEPATH="+System.getenv("JAYATANA_NATIVEPATH"));
		} else {
			// si la libreria no existe cancelar integración
			if (!new File("/usr/lib/jayatana/libjayatana.so").canRead())
				return;
			System.load("/usr/lib/jayatana/libjayatana.so");
		}
	}
}
