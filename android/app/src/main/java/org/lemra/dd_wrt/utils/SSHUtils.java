package org.lemra.dd_wrt.utils;

import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lemra.dd_wrt.api.conn.NVRAMInfo;
import org.lemra.dd_wrt.api.conn.Router;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by armel on 8/16/14.
 */
public final class SSHUtils {

    private static final String TAG = SSHUtils.class.getSimpleName();

    private SSHUtils() {
    }

    @Nullable
    public static String[] getManualProperty(@NotNull final Router router, @NotNull final String... cmdToExecute) throws Exception {
        Log.d(TAG, "getManualProperty: <router=" + router + " / cmdToExecute=" + Arrays.toString(cmdToExecute) + ">");

        @Nullable Session jschSession = null;
        @Nullable ChannelExec channelExec = null;
        @Nullable InputStream in = null;
        @Nullable InputStream err = null;
        try {
            @Nullable final String privKey = router.getPrivKey();
            @NotNull final JSch jsch = new JSch();
            if (privKey != null) {
                jsch.addIdentity(router.getUuid(), privKey.getBytes(), null, null);
            }
            jschSession = jsch.getSession(router.getUsername(), router.getRemoteIpAddress(), router.getRemotePort());
            jschSession.setPassword(router.getPassword());
            @NotNull final Properties config = new Properties();
            config.put("StrictHostKeyChecking", router.isStrictHostKeyChecking() ? "yes" : "no");
            jschSession.setConfig(config);
            jschSession.connect(30000);

            channelExec = (ChannelExec) jschSession.openChannel("exec");

            channelExec.setCommand(Joiner.on(" && ").skipNulls().join(cmdToExecute));
            channelExec.setInputStream(null);
            in = channelExec.getInputStream();
            err = channelExec.getErrStream();
            channelExec.connect();
//
//            final int exitStatus = channelExec.getExitStatus();
//            Log.d(TAG, "channelExec.getExitStatus(): " + exitStatus);

            return Utils.getLines(new BufferedReader(new InputStreamReader(in)));

        } finally {
            Closeables.closeQuietly(in);
            Closeables.closeQuietly(err);
            if (channelExec != null && channelExec.isConnected()) {
                channelExec.disconnect();
            }
            if (jschSession != null && jschSession.isConnected()) {
                jschSession.disconnect();
            }
        }

    }

    @Nullable
    public static NVRAMInfo getNVRamInfoFromRouter(@Nullable final Router router, @Nullable final String... fieldsToFetch) throws Exception {

        if (router == null) {
            return null;
        }

        final List<String> grep = Lists.newArrayList();
        if (fieldsToFetch != null) {
            for (final String fieldToFetch : fieldsToFetch) {
                if (isNullOrEmpty(fieldToFetch)) {
                    continue;
                }
                grep.add("^" + fieldToFetch + "=.*");
            }
        }

        return NVRAMParser.parseNVRAMOutput(SSHUtils.getManualProperty(router,
                "nvram show" + (grep.isEmpty() ? "" : (" | grep -E \"" +
                        Joiner.on("|").join(grep) + "\""))));
    }

}