cmake_minimum_required(VERSION 3.7.2)
project(vgs_proxy_c_example C)

set(CMAKE_C_STANDARD 99)

add_executable(vgs_proxy_c_example main.c openssl-post-vgs.c openssl-post-vgs.h libcurl-post-vgs.c libcurl-post-vgs.h)
include_directories(/usr/include/x86_64-linux-gnu)

target_link_libraries(vgs_proxy_c_example ssl crypto curl)
