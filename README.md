## 3.2 How to install the kubernetes 1.29 
### Background

Use this document if you don't want to downgrade to 1.24.


The original document was targeted on the kubernetes 1.20. However the latest k8s version is 1.29. In k8s 1.24, there was a major breaking change, which completely remove the docker support for k8s, as well as other major changes about cni plugin. That is the reason why the old document does not work.
### Step 0: install the docker engine and containerd


[reference link](https://docs.docker.com/engine/install/debian/)

*This part needs to be done on EVERY MACHINE*

This part is basically the same with the original document. If you have finished this, you can skip it.

```shell
# Intall the depedency
sudo apt-get update && sudo apt-get install ca-certificates curl gnupg lsb-release

# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```
```shell
# install
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# set up docker so that we don't need to use sudo to run it
sudo systemctl enable docker
sudo usermod -aG docker "$USER"
exit
```

### Step1: configure the containerd for the kubernetes
*This part needs to be done on EVERY MACHINE. If you skip this, your cluster will crash when installing calico*

[reference link](https://kubernetes.io/docs/setup/production-environment/container-runtimes/#containerd)


One of the breaking change introduced by k8s 1.24 was that the full removal of docker support.So we have to use the containerd for the kubernetes cluster. This step targets on the configuration of containerd


First, generate a default config for containerd.
```
containerd config default > config.toml
```
Then open this file, do the following thing;

1. Find section `[plugins."io.containerd.grpc.v1.cri".containerd.runtimes.runc]`, then in the subsection  `[plugins."io.containerd.grpc.v1.cri".containerd.runtimes.runc.options]`, change `SystemdCgroup = true`. Basically your file should look like this.

```
[plugins."io.containerd.grpc.v1.cri".containerd.runtimes.runc]
  ...
  [plugins."io.containerd.grpc.v1.cri".containerd.runtimes.runc.options]
    SystemdCgroup = true 
```

2. Make sure that cri is not included in the`disabled_plugins` list within `config.toml`

3. use this config.toml to replace the original configuration file, and restart containerd
```
sudo mv config.toml /etc/containerd/config.toml

sudo systemctl restart containerd
```
### Step2: install kubeadm 1.29
*This part needs to be done on EVERY MACHINE. If you skip this, your cluster will crash when installing calico*

[reference link](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/)

Update the apt package index and install packages needed to use the Kubernetes apt repository:
```shell 
sudo apt-get update
# apt-transport-https may be a dummy package; if so, you can skip that package
sudo apt-get install -y apt-transport-https ca-certificates curl gpg
```

Download the public signing key for the Kubernetes package repositories. The same signing key is used for all repositories so you can disregard the version in the URL. Then add source for apt.
```shell
# If the folder `/etc/apt/keyrings` does not exist, it should be created before the curl command, read the note below.
# sudo mkdir -p -m 755 /etc/apt/keyrings
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.29/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

# This overwrites any existing configuration in /etc/apt/sources.list.d/kubernetes.list
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.29/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
```

Update the apt package index, install kubelet, kubeadm and kubectl, and pin their version:
```shell
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
```

### Step 3: start the master node of kubernetes
*This part needs to be done ONLY ON MASTER NODE*

[reference link](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/create-cluster-kubeadm/)

```shell
sudo kubeadm init --pod-network-cidr=192.168.0.0/16

# setup kubectl
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

```
You will also see something like this in the output
```shell
You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 145.100.135.151:6443 --token xxxxxxxxxxxxxxxxxx \
	--discovery-token-ca-cert-hash sha256:xxxxxxxxxxxxxxxxxxxxx

```

Keep This! You will need this command later!

Now execute `kubectl get ns` you should see something like this
```
NAME              STATUS   AGE
default           Active   3h17m
kube-node-lease   Active   3h17m
kube-public       Active   3h17m
kube-system       Active   3h17m
```

### Step 4: prerequisite configuration for Calico
*This part needs to be done on EVERY MACHINE, AND IT IS VITAL. If you skip this, control plane of your cluster will crash when installing calico*

[reference link](https://docs.tigera.io/calico/latest/operations/troubleshoot/troubleshooting#configure-networkmanager)

Create the following configuration file at `/etc/NetworkManager/conf.d/calico.conf` to prevent NetworkManager from interfering with the interfaces:

```
[keyfile]
unmanaged-devices=interface-name:cali*;interface-name:tunl*;interface-name:vxlan.calico;interface-name:vxlan-v6.calico;interface-name:wireguard.cali;interface-name:wg-v6.cali
```

### Step 5. install newest Calico, the CNI plugin
*This part needs to be done ONLY ON MASTER NODE*

[reference link](https://docs.tigera.io/calico/latest/getting-started/kubernetes/self-managed-onprem/onpremises)

```
curl https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/calico.yaml -O
kubectl apply -f calico.yaml
```

Wait until the master node is ready. When master node is ready, you should see something like this when executing `kubectl get nodes`

```
NAME            STATUS   ROLES           AGE     VERSION
kubeclass-151   Ready    control-plane   3h23m   v1.29.2
```

### Step6: start the worker node.
*This part needs to be done ONLY ON WORKER NODE*

Execute the command you saw in Step 3, when executing `kubeadm init`
```
kubeadm join 145.100.135.xxx:6443 --token xxxxxxxxxxxxxxxxxx \
	--discovery-token-ca-cert-hash sha256:xxxxxxxxxxxxxxxxxxxxx
```

The wait until all the worker nodes are ready. When they are ready, you should see something like this when executing `kubectl get nodes`

```
NAME            STATUS   ROLES           AGE     VERSION
kubeclass-151   Ready    control-plane   3h23m   v1.29.2
kubeclass-186   Ready    <none>          3h20m   v1.29.2
kubeclass-187   Ready    <none>          3h20m   v1.29.2
```

Congratulations! You have got a real production-level k8s cluster installed.