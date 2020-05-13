package com.isotope11.erlanglauncher;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Decompress(private val zipFileInputStream: InputStream, private val location: String) {

    init { dirChecker("") }

    fun unzip() {
        val zin = ZipInputStream(zipFileInputStream)
        var ze: ZipEntry? = null
        while({ ze = zin.nextEntry; ze}() != null) {
            Log.d("Fragment", "Unzipping " + ze!!.name)

            if (ze!!.isDirectory) {
                dirChecker(ze!!.name)
            } else {
                val fout = FileOutputStream(location + ze!!.name)
                var read = 0
                val buffer = ByteArray(8192)
                while ({read = zin.read(buffer); read}() > 0) {
                    fout.write(buffer, 0, read)
                }

                zin.closeEntry();
                fout.close();
            }
        }
        zin.close()
    }

    private fun dirChecker(dir: String) {
        val f = File(location + dir)
        if(!f.isDirectory) { f.mkdirs() }
    }
}
