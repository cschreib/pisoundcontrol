#include <uWS/uWS.h>
#include <iostream>

int main() {
    uWS::Hub h;

    h.onConnection([&h](uWS::WebSocket<uWS::SERVER> ws, uWS::HttpRequest req) {
        std::cout << "client connected" << std::endl;
    });

    h.onDisconnection([&h](uWS::WebSocket<uWS::SERVER> ws, int code, char* message, size_t length) {
        std::cout << "client disconnected" << std::endl;
    });

    h.onMessage([](uWS::WebSocket<uWS::SERVER> ws, char* message, size_t length, uWS::OpCode opCode) {
        std::cout << "received message: " << std::string(message, length) << std::endl;
    });

    h.onHttpRequest([](uWS::HttpResponse* res, uWS::HttpRequest req, char* data, size_t length, size_t remainingBytes) {
        res->end(nullptr, 0);
    });

    h.listen(4444);
    h.run();

    return 0;
}
