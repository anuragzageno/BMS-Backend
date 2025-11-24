# Deployment Guide - BMS Backend

## Overview

This repository uses GitHub Actions to automatically build Spring Boot JAR files and deploy them to an Oracle Cloud server. The deployment system maintains an organized structure with active and archived JARs.

## Deployment Architecture

**Server:** Oracle Cloud VM 
**Deployment Structure:**
```
<DEPLOY_PATH>/
├── active/          # Current active JAR
└── archive/         # Historical JARs (last 10 kept)
```

## GitHub Actions Workflow

The workflow (`.github/workflows/build-deploy-jar.yml`) automatically:

1. ✅ Builds JAR with Maven using JDK 21
2. ✅ Archives existing JAR to archive folder with timestamp
3. ✅ Deploys new JAR to active folder via SSH/SCP
4. ✅ Maintains last 10 archived versions (auto-cleanup)
5. ✅ Uploads artifacts to GitHub Actions for download

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main`
- Manual trigger via GitHub Actions UI

---

## Setup Instructions

### Step 1: Generate SSH Key for Deployment

On your local machine or server:

```bash
# Generate SSH key pair
ssh-keygen -t ed25519 -C "github-actions-bms-backend" -f ~/.ssh/github_actions_bms

# This creates:
# - ~/.ssh/github_actions_bms (private key) - Add to GitHub Secrets
# - ~/.ssh/github_actions_bms.pub (public key) - Add to Oracle server
```

### Step 2: Add Public Key to Oracle Server

```bash
# Copy public key to Oracle server
ssh-copy-id -i ~/.ssh/github_actions_bms.pub <your-user>@140.245.31.40

# OR manually:
ssh <your-user>@140.245.31.40
mkdir -p ~/.ssh
echo "<paste-your-public-key-content>" >> ~/.ssh/authorized_keys
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

### Step 3: Create Deployment Directory on Oracle Server

```bash
ssh <your-user>@140.245.31.40

# Create deployment directories
mkdir -p /path/to/deployment/active
mkdir -p /path/to/deployment/archive

# Verify
ls -la /path/to/deployment/
```

### Step 4: Add GitHub Secrets

Navigate to: **Settings > Secrets and variables > Actions > New repository secret**

Add the following secrets:

#### 1. `ORACLE_SSH_KEY`
**Value:** Private key content from `~/.ssh/github_actions_bms`

```bash
# Copy private key content
cat ~/.ssh/github_actions_bms

# Copy the ENTIRE output including:
# -----BEGIN OPENSSH PRIVATE KEY-----
# ... key content ...
# -----END OPENSSH PRIVATE KEY-----
```

#### 2. `ORACLE_USER`
**Value:** Your SSH username on Oracle server

Example: `ubuntu` or `opc` (OCI default)

#### 3. `ORACLE_DEPLOY_PATH`
**Value:** Absolute path to deployment directory

Example: `/home/ubuntu/bms-backend` or `/opt/bms/deployments`

---

## Adding GitHub Secrets - Step by Step

1. Go to your repository: `https://github.com/anuragzageno/BMS-Backend`
2. Click **Settings** (top menu)
3. In left sidebar, expand **Secrets and variables** → Click **Actions**
4. Click **New repository secret** button
5. Add each secret:
   - Name: `ORACLE_SSH_KEY`
   - Value: [Paste private key]
   - Click **Add secret**
6. Repeat for `ORACLE_USER` and `ORACLE_DEPLOY_PATH`

---

## Testing the Deployment

### Option 1: Push to Main/Develop
```bash
git add .
git commit -m "Test deployment"
git push origin main
```

### Option 2: Manual Trigger
1. Go to **Actions** tab in GitHub
2. Select **Build and Deploy JAR to Oracle Server** workflow
3. Click **Run workflow** button
4. Select branch and click **Run workflow**

### Verify Deployment

```bash
# SSH to Oracle server
ssh <your-user>@140.245.31.40

# Check active JAR
ls -lh /path/to/deployment/active/

# Check archived JARs
ls -lh /path/to/deployment/archive/
```

---

## Running the JAR on Oracle Server

### Manual Run
```bash
cd /path/to/deployment/active
java -jar *.jar
```

### Using systemd Service (Recommended for Production)

Create service file:
```bash
sudo nano /etc/systemd/system/bms-backend.service
```

Add configuration:
```ini
[Unit]
Description=Book My Studio Backend
After=network.target

[Service]
Type=simple
User=<your-user>
WorkingDirectory=/path/to/deployment/active
ExecStart=/usr/bin/java -jar /path/to/deployment/active/*.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable bms-backend
sudo systemctl start bms-backend
sudo systemctl status bms-backend
```

### Auto-restart After Deployment (Optional)

Add this step to workflow after "Deploy JAR" step:
```yaml
- name: Restart service
  run: |
    ssh -i ~/.ssh/oracle_key ${{ secrets.ORACLE_USER }}@140.245.31.40 \
      'sudo systemctl restart bms-backend'
```

---

## Troubleshooting

### Workflow Fails with "Permission denied (publickey)"
- Verify `ORACLE_SSH_KEY` contains the complete private key
- Ensure public key is in `~/.ssh/authorized_keys` on Oracle server
- Check file permissions: `chmod 600 ~/.ssh/authorized_keys`

### "Directory not found" Error
- Verify `ORACLE_DEPLOY_PATH` is correct absolute path
- Create directories manually on server

### Build Fails
- Check if `pom.xml` is configured correctly
- Ensure Java 21 compatibility in your code
- Review build logs in GitHub Actions

### Can't Connect to Oracle Server
- Verify server IP: `140.245.31.40`
- Check Oracle Cloud security list allows SSH (port 22)
- Test SSH manually: `ssh <user>@140.245.31.40`

---

## Folder Structure After Deployment

```
/path/to/deployment/
├── active/
│   └── bms-backend-1.0.0.jar          # Current version
└── archive/
    ├── bms-backend-1.0.0-20241124_093045.jar
    ├── bms-backend-1.0.0-20241123_151230.jar
    └── ... (up to 10 recent versions)
```

---

## Next Steps

- [ ] Add GitHub Secrets (ORACLE_SSH_KEY, ORACLE_USER, ORACLE_DEPLOY_PATH)
- [ ] Configure SSH access to Oracle server
- [ ] Test workflow by pushing to main branch
- [ ] Set up systemd service for auto-start
- [ ] Configure environment variables in `.env` or application properties

---

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Oracle Cloud SSH Setup](https://docs.oracle.com/en-us/iaas/Content/Compute/Tasks/accessinginstance.htm)
- [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
