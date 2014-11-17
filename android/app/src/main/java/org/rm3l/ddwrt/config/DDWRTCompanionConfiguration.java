/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Armel S.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.rm3l.ddwrt.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rm3l.ddwrt.api.conn.Router;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by armel on 8/9/14.
 */
public class DDWRTCompanionConfiguration extends ConfigurationBase {

    private static DDWRTCompanionConfiguration mInstance;
    public Context mContext;
    private boolean mSpecific = false;

    public DDWRTCompanionConfiguration(Context paramContext) {
        this.mContext = paramContext;
    }

    public static DDWRTCompanionConfiguration getInstance(Context paramContext) {
        if (mInstance == null) {
            @NotNull final DDWRTCompanionConfiguration localConfiguration = new DDWRTCompanionConfiguration(paramContext);
            mInstance = localConfiguration;
            localConfiguration.loadCurrentRouter();
        }
        while (true) {
            mInstance.mContext = paramContext;
            return mInstance;
        }
    }

    public final void addRouter(@Nullable Router paramRouterInfo) {
        if (paramRouterInfo == null)
            paramRouterInfo = this.mCurrentRouter;
        paramRouterInfo.saveToPreferences(this);
        @NotNull Set<String> localHashSet = new HashSet<String>(getRegisteredRouters());
        localHashSet.add(paramRouterInfo.getUuid());
        getPreferences().edit().putStringSet("registeredRouters", localHashSet).commit();
        setCurrentRouter(paramRouterInfo.getUuid());
    }

    public final Set<String> getRegisteredRouters() {
        return getPreferences().getStringSet("registeredRouters", new HashSet<String>(0));
    }

    public final SharedPreferences getPreferences() {
        if (this.mSpecific)
            throw new UnsupportedOperationException("This configuration is specific to one Router");
        return PreferenceManager.getDefaultSharedPreferences(this.mContext);
    }

    public final void loadCurrentRouter() {
        String str = getPreferences().getString("currentRouterUUid", null);
        if (str != null) {
            this.mCurrentRouter = Router.loadFromPreferences(this, str);
//            this.mChallenge = null;
//            BaseRequest.resetAuthorizeLock();
        }
    }

    @Override
    public SharedPreferences getPreferences(String paramString) {
        return this.mContext.getSharedPreferences(paramString, 0);
    }

    @Override
    public int getTrackId() {
        return 0;
    }

    @Nullable
    @Override
    public ConfigurationBase obtainRouterConfig(@Nullable String paramString) {
        if (paramString == null)
            return null;
        @NotNull DDWRTCompanionConfiguration localConfiguration = new DDWRTCompanionConfiguration(this.mContext);
        localConfiguration.mCurrentRouter = Router.loadFromPreferences(localConfiguration, paramString);
        localConfiguration.mSpecific = true;
        return localConfiguration;
    }

    public final void needApiAdditionalRights(@NotNull Context paramContext, final ConfigurationBase.AskPasswordListener paramAskPasswordListener) {
        @NotNull final EditText localEditText = new EditText(paramContext);
        localEditText.setInputType(128);
        localEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        Utils.hackAlertDialogTitleLine(new AlertDialog.Builder(paramContext).setMessage(2131558654).setTitle(2131558655).setView(localEditText).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
//            public final void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//                paramAskPasswordListener.onPasswordEntered(localEditText.getText().toString());
//            }
//        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
//            public final void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//                paramAskPasswordListener.onCancel();
//            }
//        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public final void onCancel(DialogInterface paramAnonymousDialogInterface) {
//                paramAskPasswordListener.onCancel();
//            }
//        }).show());
    }

    public final void needReboot(Context paramContext, final ConfigurationBase.askRebootListener paramaskRebootListener) {
//        Utils.hackAlertDialogTitleLine(new AlertDialog.Builder(paramContext).setMessage(2131558652).setTitle(2131558650).setPositiveButton(2131558651, new DialogInterface.OnClickListener() {
//            public final void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//                paramaskRebootListener.onOk();
//                Toast.makeText(DDWRTCompanionConfiguration.this.mContext, "2131558653", Toast.LENGTH_SHORT).show();
//            }
//        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
//            public final void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//                paramaskRebootListener.onCancel();
//            }
//        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public final void onCancel(DialogInterface paramAnonymousDialogInterface) {
//                paramaskRebootListener.onCancel();
//            }
//        }).show());
    }

    public final boolean isAuthenticatedOnLocalRouter() {
        return (isConnectedOnLocalRouter()) && (getPreferences().getStringSet("registeredRouteres", new HashSet()).contains(mLocalRouterUid));
    }

    public final void setCurrentRouter(String paramString) {
        getPreferences().edit().putString("currentRouterUUid", paramString).commit();
        loadCurrentRouter();
        setNetworkValid(false);
    }

    public final void setRouterRemoteAccess(@NotNull String paramString, int paramInt) {
        if (this.mCurrentRouter != null) {
            this.mCurrentRouter.setRemoteIpAddress(paramString);
            this.mCurrentRouter.setRemotePort(paramInt);
            this.mCurrentRouter.saveToPreferences(this);
        }
    }

    public final boolean isLocal() {
        return (this.mCurrentRouter != null) && (this.mUseLocalAddress);
    }
}