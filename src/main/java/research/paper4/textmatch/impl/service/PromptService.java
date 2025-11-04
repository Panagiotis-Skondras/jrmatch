package research.paper4.textmatch.impl.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PromptService {


    public static Prompt enhanceAndStructureResume(String resume){

        String systemPrompt = "You are an AI writing assistant who does resume editing. Your task is to create additional content IF NEEDED to a resume and structure it.\\n" +
                "When given a resume you have to structure the content per categories: title, description, responsibilities, education, skills, experience, additionalInfo.\\n" +
                "If content is missing fill with minimum requirements. Do not create new category or any inner list, " +
                "Any information that do not match put it to the additionalInfo category. The categories must not be empty.\\n" +
                "You must put placeholders of ^-^ symbol between categories and descriptions" +
                "Below you will find an example of a structured resume of a software engineer.\\n" +
                "EXAMPLE:\\n" +
                """
                          ^-^TITLE^-^Software Engineer^-^
                          ^-^DESCRIPTION^-^Design, develop, and maintain software applications, ensuring their performance, quality, and responsiveness.^-^
                          ^-^RESPONSIBILITIES^-^Collaborate with a team of software developers to design, code, and test software applications.
                            Troubleshoot and debug software issues.
                            Document software design and development processes.
                            Collaborate with project managers, stakeholders, and other teams to ensure project success^-^
                          ^-^EDUCATION^-^Bachelor's degree in Computer Science, Information Technology, or a related field.
                            Master's degree or relevant certification such as Certified Software Development Professional (CSDP).^-^
                          ^-^SKILLS^-^Strong programming skills in languages such as Java, C++, Python, or other relevant languages
                            Strong problem-solving skills and ability to work in a fast-paced environment^-^
                          ^-^EXPERIENCE^-^At least 2-3 years of experience as a Software Engineer or related role.
                            Familiarity with software development methodologies such as Agile, Scrum, or Waterfall.
                            Knowledge of software development tools such as Git, JIRA, or Jenkins.
                            Excellent communication skills and ability to collaborate effectively with team members.
                            Experience developing software applications for web, mobile, or desktop platforms.
                            Experience with cloud computing platforms such as AWS, Azure, or Google Cloud.^-^
                          ^-^ADDITIONAL_INFO^-^Speak fluently the English language.Have a valid driver's license.^-^\\n
                        """
                +
                "Do not include any explanations, follow this format without deviation.";

        String userPromptRewrite = "Rewrite AND structure per categories the resume below: ";
        String finalUserInput = userPromptRewrite.concat(resume);

        Message userMessage = new UserMessage(finalUserInput);
        Message systemMessage = new SystemMessage(systemPrompt);

       return new Prompt(List.of(userMessage, systemMessage));
    }

    public static Prompt enhanceAndStructureJobPost(String jobPost){

        String systemPrompt = "You are an AI writing assistant who does job post editing. Your task is to create additional content IF NEEDED to a job post and structure it.\\n" +
                "When given a job post you have to structure the content per categories: title, description, responsibilities, education, skills, experience, additionalInfo.\\n" +
                "If content is missing fill with minimum requirements. Do not create new category or any inner list, " +
                "Any information that do not match put it to the additionalInfo category. The categories must not be empty.\\n" +
                "You must put placeholders of ^-^ symbol between categories and descriptions" +
                "Below you will find an example of a structured job post of a software engineer.\\n" +
                "EXAMPLE:\\n" +
                """
                          ^-^TITLE^-^Software Engineer^-^
                          ^-^DESCRIPTION^-^We are seeking a highly motivated and skilled Software Engineer to join our growing development team. In this role, you'll work on building scalable, high-performance applications and services that power our core business functions.
                             This is a great opportunity to work with a passionate team in a fast-paced, collaborative environment, and to contribute to impactful software solutions.^-^
                          ^-^RESPONSIBILITIES^-^As a Software Engineer, you will design, develop, test, and deploy software solutions across the full technology stack.
                          You will collaborate closely with product managers, designers, and fellow engineers to translate requirements into high-quality features.
                          Writing clean, maintainable, and well-documented code will be a key part of your responsibilities. You will also participate in peer code reviews, troubleshoot and debug existing systems, and ensure the overall performance, responsiveness, and reliability of our applications.
                          Staying informed about emerging technologies and applying them effectively to our systems is encouraged.^-^
                          ^-^EDUCATION^-^A bachelor’s degree in Computer Science, Software Engineering, or a related field is required. Equivalent practical experience will also be considered.^-^
                          ^-^SKILLS^-^The ideal candidate is proficient in at least one modern programming language such as Java, Python, JavaScript, or C#.
                          A strong understanding of data structures, algorithms, and object-oriented programming principles is essential.
                          Experience working with RESTful APIs, microservice architecture, and relational or NoSQL databases is expected.
                          Familiarity with version control systems like Git, and exposure to popular frameworks such as Spring Boot, Django, or Node.js will be advantageous.
                          Candidates should demonstrate strong analytical and communication skills, along with the ability to work both independently and collaboratively.^-^
                          ^-^EXPERIENCE^-^We are looking for candidates with at least two years of professional software development experience.
                          Hands-on involvement in the full software development lifecycle—including design, implementation, testing, and deployment—is required.
                          Experience working in Agile or Scrum-based teams is preferred, and exposure to cloud platforms like AWS, Azure, or Google Cloud is a plus.^-^
                          ^-^ADDITIONAL_INFO^-^This is a full-time position, and the role can be based on-site, hybrid, or remote depending on your location and preference.
                          We offer a competitive salary along with a comprehensive benefits package.
                          The company provides opportunities for professional growth, continued learning, and career advancement.
                          You will be joining a diverse and inclusive team that values collaboration, creativity, and curiosity.^-^\\n
                        """
                +
                "Do not include any explanations, follow this format without deviation.";

        String userPromptRewrite = "Rewrite AND structure per categories the job post below: ";
        String finalUserInput = userPromptRewrite.concat(jobPost);

        Message userMessage = new UserMessage(finalUserInput);
        Message systemMessage = new SystemMessage(systemPrompt);

        return new Prompt(List.of(userMessage, systemMessage));
    }


    public static Prompt regenerateContent(){

        String basicPrompt = "Your previous answer did not included one of the categories of: title, description, responsibilities, education, skills, experience, additionalInfo.\\n"+
                "Regenerate the previous output and fill with text all the categories.\\n If you do not have data fill the category with minimum requirements per job or resume."+
                "You must put placeholders of ^-^ symbol between categories and descriptions as you did in previous answer";

        Message userMessage = new UserMessage(basicPrompt);
        return new Prompt(List.of(userMessage));
    }

    public static Prompt commentOnResults(Boolean isResume, String text){

        String inputEntity = isResume ? " resumes. " : " job posts. ";
        String iEntity = isResume ? " resume. " : " job post. ";

        String basicPrompt = "In previous answers you structure and generate" + inputEntity +
                ". Below you will find an example of a structured" + iEntity + "of a software engineer.\\n " +
                "EXAMPLE:\\n"+
                PromptService.entityToAdd(isResume)
                + "The following" + inputEntity + " were ranked based on text matching techniques.\\n" +
                "Explain the ranking and the advantages of the following ranking\\n"+
                "RANKING:\\n" + text;

        Message userMessage = new UserMessage(basicPrompt);
        return new Prompt(List.of(userMessage));
    }

    public static String entityToAdd(Boolean isResume){

        if(isResume){
            return """
                          TITLE:Software Engineer
                          DESCRIPTION:Design, develop, and maintain software applications, ensuring their performance, quality, and responsiveness.
                          RESPONSIBILITIES:Collaborate with a team of software developers to design, code, and test software applications.
                            Troubleshoot and debug software issues.
                            Document software design and development processes.
                            Collaborate with project managers, stakeholders, and other teams to ensure project success
                          EDUCATION:Bachelor's degree in Computer Science, Information Technology, or a related field.
                            Master's degree or relevant certification such as Certified Software Development Professional (CSDP).
                          SKILLS:Strong programming skills in languages such as Java, C++, Python, or other relevant languages
                            Strong problem-solving skills and ability to work in a fast-paced environment^-^
                          EXPERIENCE:At least 2-3 years of experience as a Software Engineer or related role.
                            Familiarity with software development methodologies such as Agile, Scrum, or Waterfall.
                            Knowledge of software development tools such as Git, JIRA, or Jenkins.
                            Excellent communication skills and ability to collaborate effectively with team members.
                            Experience developing software applications for web, mobile, or desktop platforms.
                            Experience with cloud computing platforms such as AWS, Azure, or Google Cloud.
                          ADDITIONAL_INFO:Speak fluently the English language.Have a valid driver's license.\\n
                        """;
        }

        return """
                        TITLE:Software Engineer
                        DESCRIPTION:We are seeking a highly motivated and skilled Software Engineer to join our growing development team. In this role, you'll work on building scalable, high-performance applications and services that power our core business functions.
                             This is a great opportunity to work with a passionate team in a fast-paced, collaborative environment, and to contribute to impactful software solutions.
                        RESPONSIBILITIES:As a Software Engineer, you will design, develop, test, and deploy software solutions across the full technology stack.
                          You will collaborate closely with product managers, designers, and fellow engineers to translate requirements into high-quality features.
                          Writing clean, maintainable, and well-documented code will be a key part of your responsibilities. You will also participate in peer code reviews, troubleshoot and debug existing systems, and ensure the overall performance, responsiveness, and reliability of our applications.
                          Staying informed about emerging technologies and applying them effectively to our systems is encouraged.
                        EDUCATION:A bachelor’s degree in Computer Science, Software Engineering, or a related field is required. Equivalent practical experience will also be considered.
                        SKILLS^-^The ideal candidate is proficient in at least one modern programming language such as Java, Python, JavaScript, or C#.
                          A strong understanding of data structures, algorithms, and object-oriented programming principles is essential.
                          Experience working with RESTful APIs, microservice architecture, and relational or NoSQL databases is expected.
                          Familiarity with version control systems like Git, and exposure to popular frameworks such as Spring Boot, Django, or Node.js will be advantageous.
                          Candidates should demonstrate strong analytical and communication skills, along with the ability to work both independently and collaboratively.
                        EXPERIENCE:We are looking for candidates with at least two years of professional software development experience.
                          Hands-on involvement in the full software development lifecycle—including design, implementation, testing, and deployment—is required.
                          Experience working in Agile or Scrum-based teams is preferred, and exposure to cloud platforms like AWS, Azure, or Google Cloud is a plus.
                        ADDITIONAL_INFO:This is a full-time position, and the role can be based on-site, hybrid, or remote depending on your location and preference.
                          We offer a competitive salary along with a comprehensive benefits package.
                          The company provides opportunities for professional growth, continued learning, and career advancement.
                          You will be joining a diverse and inclusive team that values collaboration, creativity, and curiosity.\\n
                        """;
    }

    public static String convertMapEntriestoString(Map<String, ?> map) {
        return map.keySet().stream()
                .map(key -> key + ":" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}\\n"));

    }
}

