package com.isotope11.erlanglauncher

import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.InputStream

object Utils {
    fun listFiles(path: String) {
        NativeInterface.startTask("/system/bin/ls -al $path")
    }

    fun copyErlangIntoDataDir(context: Context, filename: String, location: String) {
        Log.d("COPY", "copyErlangIntoDataDir start")

        val erlangZipFileInputStream = context.assets.open(filename)
        val unzipper = Decompress(erlangZipFileInputStream, location)
        unzipper.unzip()

        Log.d("COPY", "copyErlangIntoDataDir done")
    }

    fun makeExecutable(path: String) {
        NativeInterface.startTask("/system/bin/chmod 744 /data/data/com.isotope11.erlanglauncher/files/$path")
    }

    fun launchEpmd() {
        NativeInterface.startTask("/data/data/com.isotope11.erlanglauncher/files/erlang_23.0_arm/bin/epmd -daemon 2>&1")
    }

    fun listEpmd() {
        NativeInterface.startTask("/data/data/com.isotope11.erlanglauncher/files/erlang_23.0_arm/erts-10.7.2/bin/epmd -names")
    }

    fun launchErlangNative(serverName: String) {
        NativeInterface.startTask(arrayOf(
                "data/data/com.isotope11.erlanglauncher/files/erlang_23.0_arm/bin/erl",
                        "-detached",
                        "-sname $serverName",
                        "-setcookie test",
                        "-pa data/data/com.isotope11.erlanglauncher/files/",
                        "-s hello_jinterface 2>&1").reduce { acc, el -> "$acc $el" })
    }

    fun listProcesses() {
        NativeInterface.startTask("/system/bin/ps -A")
    }

    fun copyErlangServerCode(context: Context) {
        val erlangServerCodeInputStream = context.assets.open("hello_jinterface.beam")
        val out = context.openFileOutput("hello_jinterface.beam", Context.MODE_PRIVATE)
        var read: Int
        val buffer = ByteArray(8192)
        while (erlangServerCodeInputStream.read(buffer).also { read = it } > 0) {
            out.write(buffer, 0, read)
        }
        erlangServerCodeInputStream.close()
        out.flush()
        out.close()
    }
}