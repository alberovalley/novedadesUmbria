package com.alberovalley.novedadesumbria.service.task;

import android.content.Context;

import com.alberovalley.novedadesumbria.comm.UmbriaLoginData;
import com.alberovalley.novedadesumbria.comm.data.UmbriaData;

public abstract class UmbriaNovedadesTask implements UmbriaTask{

	public abstract UmbriaData getNovedades(UmbriaLoginData ld, Context ctx);
}
