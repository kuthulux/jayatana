# Copyright (c) 2014 Jared González
#
# Permission is hereby granted, free of charge, to any
# person obtaining a copy of this software and associated
# documentation files (the "Software"), to deal in the
# Software without restriction, including without limitation
# the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the
# Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice
# shall be included in all copies or substantial portions of
# the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
# KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
# OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
# OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
# OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
# SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

# CONFIGURACION DE JAVA
# Obtener arquitectura de compilación
ARCH=$(shell getconf LONG_BIT)
ifeq (${ARCH},64)
JAVA_ARCH=amd64
else
JAVA_ARCH=i386
endif
# Obtener versión Java de compilación
ifndef JAVA_HOME
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-${JAVA_ARCH}
endif
# Eliminar la variable de ambiente JAVA_TOOL_OPTIONS
unexport JAVA_TOOL_OPTIONS

# Compilar paquete Java
all:
	ant -DJavaHome=${JAVA_HOME}

clean:
	ant -DJavaHome=${JAVA_HOME} clean
