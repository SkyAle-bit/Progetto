import os, re
src_dir = r'C:\Users\ninoa\Desktop\Progetto\tesi\src\main\java'
print("--- PROBLEM 1 ---")
classes_to_fix = [
    'ReviewResponse', 'ProfessionalSummaryDTO', 'ClientDashboardResponse',
    'ConversationPreviewResponse', 'ClientBasicInfoResponse', 'ChatMessageResponse',
    'SendMessageRequest', 'RegisterRequest', 'ErrorResponse'
]
for root, dirs, files in os.walk(src_dir):
    for f in files:
        if f.replace('.java', '') in classes_to_fix:
            p = os.path.join(root, f)
            with open(p, 'r', encoding='utf-8') as file:
                c = file.read()
            class_name = f.replace('.java', '')
            c = c.replace('import lombok.Builder;', '')
            c = c.replace('import lombok.AllArgsConstructor;', '')
            c = c.replace('import lombok.NoArgsConstructor;', '')
            c = re.sub(r'@Builder\s*\n', '', c)
            c = re.sub(r'@AllArgsConstructor\s*\n', '', c)
            c = re.sub(r'@NoArgsConstructor\s*\n', '', c)
            # Remove any existing manual builder if script ran partially
            if 'public static class Builder' in c:
                continue
            fields = re.findall(r'private\s+([\w<>\[\]\?\.,\s]+?)\s+([a-zA-Z0-9_]+)\s*;', c)
            # If the class already has a constructor or Builder, proceed carefully.
            builder_code = f'\n    private {class_name}() {{}}\n\n'
            builder_code += f'    public static class Builder {{\n'
            for t, n in fields:
                builder_code += f'        private {t.strip()} {n};\n'
            for t, n in fields:
                builder_code += f'\n        public Builder {n}({t.strip()} {n}) {{\n            this.{n} = {n};\n            return this;\n        }}\n'
            builder_code += f'\n        public {class_name} build() {{\n            {class_name} obj = new {class_name}();\n'
            for _, n in fields:
                builder_code += f'            obj.{n} = this.{n};\n'
            builder_code += '            return obj;\n        }\n    }\n\n'
            builder_code += f'    public static Builder builder() {{\n        return new Builder();\n    }}\n'
            # find last brace
            idx = c.rfind('}')
            if idx != -1:
                c = c[:idx] + builder_code + '}\n'
            with open(p, 'w', encoding='utf-8') as file:
                file.write(c)
print("--- PROBLEM 3 ---")
facades = ['AdminFacade', 'UserFacade']
for facade in facades:
    facade_path = ''
    for root, dirs, files in os.walk(src_dir):
        if facade + '.java' in files:
            facade_path = os.path.join(root, facade + '.java')
            break
    if not facade_path: continue
    with open(facade_path, 'r', encoding='utf-8') as f:
        content = f.read()
    method_pattern = re.compile(r'public\s+(?!class)([\w<>\[\]\?]+)\s+(\w+)\s*\((.*?)\)(\s*throws\s+[\w\s,]+)?\s*\{')
    methods = method_pattern.findall(content)
    interface_name = 'I' + facade
    interface_dir = os.path.dirname(facade_path)
    interface_path = os.path.join(interface_dir, interface_name + '.java')
    package_match = re.search(r'package\s+([\w\.]+);', content)
    package_decl = package_match.group(0) if package_match else ''
    imports = set(re.findall(r'import\s+[\w\.]+;', content))
    import_decl = '\n'.join(imports)
    interface_code = f'{package_decl}\n\n{import_decl}\n\npublic interface {interface_name} {{\n'
    for m in methods:
        ret_type, m_name, args, throws_decl = m
        interface_code += f'    {ret_type} {m_name}({args}){throws_decl or ""};\n'
    interface_code += '}\n'
    with open(interface_path, 'w', encoding='utf-8') as f:
        f.write(interface_code)
    if f'implements {interface_name}' not in content:
        content = re.sub(f'public class {facade}((?:\\s+implements\\s+[\\w\\s,]+)?)', f'public class {facade}\\1 implements {interface_name}', content)
        content = content.replace(f'implements implements {interface_name}', f'implements {interface_name}')
        with open(facade_path, 'w', encoding='utf-8') as f:
            f.write(content)
for root, dirs, files in os.walk(src_dir):
    for f in files:
        if f.endswith('Controller.java'):
            p = os.path.join(root, f)
            with open(p, 'r', encoding='utf-8') as file:
                c = file.read()
            changed = False
            for facade in facades:
                controller_name = f.replace('.java', '')
                regex_inject = rf'private final {facade}\s+(\w+);'
                if re.search(regex_inject, c):
                    c = re.sub(rf'private final {facade}\s+(\w+);', rf'private final I{facade} \1;', c)
                    c = re.sub(rf'public\s+{controller_name}\s*\((.*?){facade}\s+(\w+)(.*?)\)', rf'public {controller_name}(\1I{facade} \2\3)', c)
                    c = c.replace(f'import com.project.tesi.facade.{facade};', f'import com.project.tesi.facade.{facade};\nimport com.project.tesi.facade.I{facade};')
                    changed = True
            if changed:
                with open(p, 'w', encoding='utf-8') as file:
                    file.write(c)
print("--- PROBLEM 5 ---")
# profilePictureUrl seems commonly unused or profilePicture is. We will add java doc
user_java_path = os.path.join(src_dir, 'com', 'project', 'tesi', 'model', 'User.java')
if os.path.exists(user_java_path):
    with open(user_java_path, 'r', encoding='utf-8') as f:
         c = f.read()
    if 'profilePicture' in c and 'profilePictureUrl' in c:
         jd = """
    /**
     * Rappresenta l'immagine profilo in base64 (o percorso locale).
     */"""
         ju = """
    /**
     * Rappresenta l'URL esterno per l'immagine profilo (es. caricata su cloud).
     */"""
         c = c.replace('private String profilePicture;', jd + '\n    private String profilePicture;')
         c = c.replace('private String profilePictureUrl;', ju + '\n    private String profilePictureUrl;')
         with open(user_java_path, 'w', encoding='utf-8') as f:
             f.write(c)
print("DONE")
