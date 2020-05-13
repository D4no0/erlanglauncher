package com.isotope11.erlanglauncher

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ericsson.otp.erlang.*
import com.isotope11.erlanglauncher.Utils.copyErlangIntoDataDir
import com.isotope11.erlanglauncher.Utils.copyErlangServerCode
import com.isotope11.erlanglauncher.Utils.launchErlangNative
import com.isotope11.erlanglauncher.Utils.listFiles
import com.isotope11.erlanglauncher.Utils.listProcesses
import com.isotope11.erlanglauncher.Utils.makeExecutable
import kotlinx.coroutines.*
import java.io.*
import kotlin.coroutines.CoroutineContext

class MainActivity: Activity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        copyErlangIntoDataDir(this, "erlang_23.0_arm.zip", "/data/data/com.isotope11.erlanglauncher/files/") // Need to make this optional, check if it's there, or something...
        makeExecutable()
        copyErlangServerCode(this)

        listFiles("/data/data/com.isotope11.erlanglauncher/files/erlang_23.0_arm/erts-10.7.2/bin")
        launchErlangNative("server1")
        listProcesses()

        CoroutineScope(Dispatchers.Default).launch {
            jniInterfaceTester("server1")
        }
    }

    private fun makeExecutable() {
        makeExecutable("erlang_23.0_arm/bin/erl")
        makeExecutable("erlang_23.0_arm/erts-10.7.2/bin/erlexec")
        makeExecutable("erlang_23.0_arm/erts-10.7.2/bin/beam.smp")
        makeExecutable("erlang_23.0_arm/erts-10.7.2/bin/erl_child_setup")
        makeExecutable("erlang_23.0_arm/bin/epmd")
        makeExecutable("erlang_23.0_arm/erts-10.7.2/bin/inet_gethost")
    }

    private fun jniInterfaceTester(serverName: String) {
        val self = OtpNode("mynode", "test")
        val mbox = self.createMbox("facserver")

        while (!self.ping(serverName, 4000)) {
            Log.d("JNInterface", "remote is not up");
        }
        Log.d("JNInterface", "remote is up!!!!");

        val msg = arrayOf(mbox.self(), OtpErlangAtom("ping"))
        val tuple = OtpErlangTuple(msg)
        mbox.send("pong", serverName, tuple)

        while (true) {
            val robj = mbox.receive();
            val rtuple = robj as OtpErlangTuple
            val fromPid = rtuple.elementAt(0) as OtpErlangPid
            val rmsg = rtuple.elementAt(1)

            println("Message: " + rmsg + " received from:  "
                    + fromPid.toString());

            val ok = OtpErlangAtom("stop");
            mbox.send(fromPid, ok);
            break;
        }
    }
}