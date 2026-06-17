import os
import zipfile

def zip_dir(src, dest, exclude_dirs=None):
    with zipfile.ZipFile(dest, 'w', zipfile.ZIP_DEFLATED) as z:
        for root, dirs, files in os.walk(src):
            if exclude_dirs:
                dirs[:] = [d for d in dirs if d not in exclude_dirs]
            for file in files:
                file_path = os.path.join(root, file)
                arcname = os.path.relpath(file_path, src)
                z.write(file_path, arcname)

# Zip app directory, exclude 'build' folder
zip_dir('app', 'app.zip', exclude_dirs=['build'])
# Zip web directory (no exclusions)
zip_dir('web', 'web.zip')
print('Zipping completed.')
