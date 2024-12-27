name: qqbot
services:
    qqbot:
        image: mlikiowa/napcat-docker
        ports:
            - "3000:3000"
            - "3001:3001"
            - "6099:6099"
            - "25511:25511"
        container_name: qqbot
        network_mode: bridge
        restart: always
        volumes:
            - /opt/QQData/QQ:/app/.config/QQ
            - /opt/QQData/config:/app/napcat/config