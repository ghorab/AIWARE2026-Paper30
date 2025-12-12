apiVersion: v1
kind: PersistentVolume
metadata:
  name: concourse-postgresql-0
  labels:
    type: amazonEBS
    app: postgresql
    release: concourse
    role: master
spec:
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  storageClassName: postgresql-storage-class
  awsElasticBlockStore:
    volumeID: ${VOLUME_ID}
#    fsType: gp2
    fsType: ext4