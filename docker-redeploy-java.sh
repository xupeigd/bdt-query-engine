# !bnin/bash
docker compose -f compile.yml up && docker rm -f bdt-query-engine && docker rmi -f bdt-query-engine:1.0 && docker compose up bdt-query-engine &