import React from 'react'
import clsx from 'clsx'
import styles from './styles.module.css'

const FeatureList = [
    {
        title: 'Easy to Use',
        Svg: () => <img src={require('./screenshot-construction.png').default} alt="Building a datacenter in OpenDC" />,
        description: (
            <>
                OpenDC is designed from the ground up to be easily installed and used via its online interface to get
                your experiments running quickly.
            </>
        ),
    },
    {
        title: 'Versatile Models',
        Svg: () => (
            <img src={require('./screenshot-explore.png').default} alt="Explore alternative scenarios with OpenDC" />
        ),
        description: (
            <>
                Explore scenarios around emerging datacenter technologies such as <em>cloud computing</em>,{' '}
                <em>serverless computing</em>, <em>big data</em>, and <em>machine learning</em>.
            </>
        ),
    },
    {
        title: 'Simplified Analysis',
        Svg: () => (
            <img
                src={require('./screenshot-results.png').default}
                alt="Automated plots and visual summaries generated by OpenDC"
            />
        ),
        description: (
            <>
                Investigate datacenter performance using the automated plots and visual summaries provided
                out-of-the-box by OpenDC.
            </>
        ),
    },
]

function Feature({ Svg, title, description }) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img" />
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>
    )
}

export default function HomepageFeatures() {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    )
}
