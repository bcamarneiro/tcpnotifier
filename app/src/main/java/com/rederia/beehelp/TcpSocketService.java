package com.rederia.beehelp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TcpSocketService extends Service {
    public TcpSocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
