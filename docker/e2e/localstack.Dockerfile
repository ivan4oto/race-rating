FROM localstack/localstack

# Copy init script and set permissions
COPY localstack-init.sh /etc/localstack/init/ready.d/init-buckets.sh
RUN chmod +x /etc/localstack/init/ready.d/init-buckets.sh