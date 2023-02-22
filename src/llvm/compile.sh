clang++ -O3 -c $(llvm-config --cxxflags) main.cpp -o main.o
clang++ main.o $(llvm-config --ldflags --libs) -lpthread -lncurses -o main
