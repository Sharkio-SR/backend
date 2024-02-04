git checkout developp
git pull

docker container prune -f

docker build -t sharkio/backend .
docker run -d --name backend -p 8080:8080 sharkio/backend
