ssh crepusculumx@121.36.210.113 "rm -rf /home/crepusculumx/server"
scp -r ../prod crepusculumx@121.36.210.113:/home/crepusculumx/server/

# podman_run.prod.bash内部依赖工作目录
ssh crepusculumx@121.36.210.113 "
ln -s /home/crepusculumx/crepus-server-resources/blog /home/crepusculumx/server/resources/public ;
cd /home/crepusculumx/server ;
bash /home/crepusculumx/server/podman_run.prod.bash rework"
