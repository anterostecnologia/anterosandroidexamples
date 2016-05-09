/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.vendas;

import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


public class BackupService extends Service {

	private static final int INTERVALO_BAKCUP = 2;// horas
	private static final int TEMPO_INATIVIDADE = 5; // minutos
	public static final String DH_ULTIMO_BKP = "DH_ULTIMO_BKP";
	public static final String PREFERENCES_NAME = "Backup-Service";
	private boolean serviceRunning = false;
	private boolean screenOff = false;
	private final String databaseName = "vendas.db";
	private Handler handler;

	private SharedPreferences preferences;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

		handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					ExportDatabaseTask.executarBakcup(databaseName, BackupService.this
							.getApplication().getDatabasePath(databaseName)
							.getAbsolutePath(), preferences, BackupService.this);
					//Log.v("backup service", "rodou bakcup");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					screenOff = true;
					//Log.v("backup service", "apagou");
					try {
						new Thread(new BackupThread()).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					screenOff = false;
					//Log.v("backup service", "acendeu");
				}
			}
		};
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//Log.v("backup service", "start");
		try {
			new Thread(new BackupThread()).start();
			serviceRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			serviceRunning = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.v("backup service", "destroy");
		serviceRunning = false;
	}

	private class BackupThread implements Runnable {

		@Override
		public void run() {
			//Log.v("backup service", "run");

			try {
				if (screenOff) {
					//Log.v("backup service", "tela apagada");
					/*
					 * Aguarda o intervalo definido
					 */
					try {
						Thread.sleep(TEMPO_INATIVIDADE * 60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/*
					 * Verifica quando foi efetuado o último bakcup em horas
					 */
					//Log.v("backup service", "ultimo bkp: " + new Date(preferences.getLong(DH_ULTIMO_BKP, 0)));
					if (screenOff && DateFormat.differenceInHours(
							new Date(preferences.getLong(DH_ULTIMO_BKP, 0)), new Date()) > INTERVALO_BAKCUP) {
						// é preciso utilizar o handler para se comunicar
						// entre duas threads
						//Log.v("backup service", "chamou handle");
						handler.sendMessage(new Message());
						return;
					}
					/*
					 * Inicia o processo novamente
					 */
					if (serviceRunning) {
						new Thread(new BackupThread()).start();
					}
				}
				//else
					//Log.v("backup service", "tela acesa");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}