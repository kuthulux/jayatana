all:
	cd libjayatana ; make
	cd libjayatanaag ; make
	cd jayatana ; make
	cd jayatanaag ; make

clean:
	cd libjayatana ; make clean
	cd libjayatanaag ; make clean
	cd jayatana ; make clean
	cd jayatanaag ; make clean

