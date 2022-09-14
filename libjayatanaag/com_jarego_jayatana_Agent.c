/*
 * Copyright (c) 2014 Jared González
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * File:   com_jarego_jayatana_Agent.c
 * Author: Jared González
 */
#include <jvmti.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <X11/Xlib.h>

#include "com_jarego_jayatana_Agent.h"

/**
 * Inicializar observadores de componentes para prevenir la integración
 * con Ubuntu/Linux.
 */
jvmtiError com_jarego_jayatana_Initialize(JavaVM *vm, int fromAgent);

/**
 * Validar el valor de una variable de ambiente para más opciones o
 * variables de configuración.
 */
int com_jarego_jayatana_Agent_CheckEnv(const char *envname, const char *envval, const int def);

/**
 * Iniciar observador de inicio de hilos para prevenir la integración
 * con Ubuntu/Linux
 */
static void JNICALL
com_jarego_jayatana_Agent_threadStart(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread) {
	// recuperar información del hilo
	jvmtiError error;
	jvmtiThreadInfo info;
	error = (*jvmti_env)->GetThreadInfo(jvmti_env, thread, &info);
	if (error == JVMTI_ERROR_NONE) {
		// inicializar XInitThreads para corregir defecto en OpenJDK 6 para los hilos de AWT o
		// Java 2D
		if (strcmp(info.name, "Java2D Disposer") == 0) {
			// inicializar hilos de X, solo para OpenJDK 6
			char *version = 0;
			if ((*jvmti_env)->GetSystemProperty(
					jvmti_env, "java.vm.specification.version", &version) == JVMTI_ERROR_NONE) {
				if (strcmp(version, "1.0") == 0) {
					// TODO: Utilizando openjdk6, al actualizar el objeto
					// splashScreen (splashScreen.update) la aplicacion muere.
					// Existe un conflicto al utilizar XInitThread y pthread.
					// Error:
					//   java: pthread_mutex_lock.c:317: __pthread_mutex_lock_full: La declaración `(-(e)) != 3 || !robust' no se cumple.
					XInitThreads();
				}
				(*jvmti_env)->Deallocate(jvmti_env, (unsigned char*)version);
			}
		} else if (strcmp(info.name, "AWT-XAWT") == 0) {
			// instala la clase para control de integración Swing
			jclass clsInstallers = (*jni_env)->FindClass(
					jni_env, "com/jarego/jayatana/FeatureManager");
			if (clsInstallers != NULL) {
				jmethodID midInstallForSwing = (*jni_env)->GetStaticMethodID(
						jni_env, clsInstallers, "deployForSwing", "()V");
				(*jni_env)->CallStaticVoidMethod(jni_env, clsInstallers, midInstallForSwing);
				(*jni_env)->DeleteLocalRef(jni_env, clsInstallers);
			}
			// una vez inicializada la prueba
			(*jvmti_env)->SetEventNotificationMode(jvmti_env,
				JVMTI_DISABLE, JVMTI_EVENT_THREAD_START, (jthread)NULL);
		}
	}
}

/**
 * Cargar agente para integración con Ubuntu/Linux
 */
JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
	return com_jarego_jayatana_Initialize(vm, 1);
}

/**
 * Carga libreria nativa desde agente Java para integración
 * con Ubuntu/Linux
 */
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
	com_jarego_jayatana_Initialize(vm, 0);
	return JNI_VERSION_1_6;
}

/**
 * Inicializar observadores de componentes para prevenir la integración
 * con Ubuntu/Linux.
 */
jvmtiError com_jarego_jayatana_Initialize(JavaVM *vm, int fromAgent) {
	// en caso de no exista una referencia a jayatana
	if (getenv("JAYATANA_CLASSPATH") == NULL &&
			access("/usr/share/java/jayatana.jar", R_OK) != 0)
		return JVMTI_ERROR_NONE;

	// la librería si existe iniciar procedo de validación de integración
	if (com_jarego_jayatana_Agent_CheckEnv("XDG_CURRENT_DESKTOP", "Unity", False) ?
			com_jarego_jayatana_Agent_CheckEnv("JAYATANA_FORCE", "true", True) &&
			com_jarego_jayatana_Agent_CheckEnv("JAYATANA", "1", True) :
			com_jarego_jayatana_Agent_CheckEnv("JAYATANA_FORCE", "true", False) ||
			com_jarego_jayatana_Agent_CheckEnv("JAYATANA", "1", False)) {

		// inicializar entorno
		jvmtiEnv *jvmti_env;
		(*vm)->GetEnv(vm, (void**) &jvmti_env, JVMTI_VERSION);

		// recuperar version
		char *version = 0;
		if ((*jvmti_env)->GetSystemProperty(
				jvmti_env, "java.vm.version", &version) == JVMTI_ERROR_NONE) {

			// ignorar para versiones 1.4 y 1.5
			if (strncmp(version, "1.4", 3) != 0 && strncmp(version, "1.5", 3) != 0) {

				// activar capacidades
				jvmtiCapabilities capabilities;
				memset(&capabilities, 0, sizeof(jvmtiCapabilities));
				//capabilities.can_generate_method_exit_events = 1;
				(*jvmti_env)->AddCapabilities(jvmti_env, &capabilities);

				// registrar funciones de eventos
				jvmtiEventCallbacks callbacks;
				memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));
				//callbacks.MethodExit = &com_jarego_jayatana_Agent_MethodExit;
				callbacks.ThreadStart = &com_jarego_jayatana_Agent_threadStart;
				(*jvmti_env)->SetEventCallbacks(jvmti_env,
						&callbacks, (jint)sizeof(jvmtiEventCallbacks));

				// habilitar gestor de eventos
				(*jvmti_env)->SetEventNotificationMode(jvmti_env,
						JVMTI_ENABLE, JVMTI_EVENT_THREAD_START, (jthread)NULL);
				//(*jvmti_env)->SetEventNotificationMode(jvmti_env,
				//		JVMTI_ENABLE, JVMTI_EVENT_METHOD_EXIT, (jthread)NULL);

				// cargar ruta de clases jayatana
				if (getenv("JAYATANA_CLASSPATH") != NULL) {// opción para desarrollo
					(*jvmti_env)->AddToSystemClassLoaderSearch(
							jvmti_env, getenv("JAYATANA_CLASSPATH"));
					fprintf(stderr, "JAYATANA_CLASSPATH=%s\n", getenv("JAYATANA_CLASSPATH"));
				} else {
					(*jvmti_env)->AddToSystemClassLoaderSearch(
							jvmti_env, "/usr/share/java/jayatana.jar");
				}
			}

			// liberar cadena de versión
			(*jvmti_env)->Deallocate(jvmti_env, (unsigned char*)version);
		}
	}
	return JVMTI_ERROR_NONE;
}

/**
 * Validar el valor de una variable de ambiente para más opciones o
 * variables de configuración.
 */
int com_jarego_jayatana_Agent_CheckEnv(const char *envname, const char *envval, const int def) {
	if (getenv(envname) == NULL) return def;
	else if (strcmp(getenv(envname), envval) == 0) return True;
	else return False;
}

